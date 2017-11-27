/*******************************************************************************
 * Copyright (c) 2010 Daniel Murygin <dm[at]sernet[dot]de>.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.iso27k.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sernet.gs.ui.rcp.main.Activator;
import sernet.gs.ui.rcp.main.common.model.CnAElementFactory;
import sernet.gs.ui.rcp.main.preferences.PreferenceConstants;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.iso27k.Group;
import sernet.verinice.service.commands.templates.LoadModelingTemplateSettings;

/**
 * A CopyService is a job, which
 * copies a list of elements to an Element-{@link Group}.
 * The progress of the copy process can be monitored by a {@link IProgressObserver}.
 * 
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class CopyService extends PasteService implements IProgressTask {
	
	private final Logger log = Logger.getLogger(CopyService.class);
	
	
    
	private final List<CnATreeElement> elements;
	
	private List<String> newElements;
    
    private boolean copyLinks = false;
    
    private boolean copyAttachments = false;
    
	/**
     * Creates a new CopyService
     * 
     * @param progressObserver used to monitor the job process
     * @param group an element group, elements are copied to this group
     * @param elementList a list of elements
     */
    @SuppressWarnings("unchecked")
    public CopyService(final CnATreeElement group, final List<CnATreeElement> elementList) {
        progressObserver = new DummyProgressObserver();
        this.selectedGroup = group;
        this.elements = elementList;    
    }
	
	/**
	 * Creates a new CopyService
	 * 
	 * @param progressObserver used to monitor the job process
	 * @param group an element group, elements are copied to this group
	 * @param elementList a list of elements
	 * @param copyLinks 
	 */
	public CopyService(final IProgressObserver progressObserver, final CnATreeElement group, final List<CnATreeElement> elementList, final boolean copyLinks) {
		this.progressObserver = progressObserver;
		this.selectedGroup = group;
		this.elements = elementList;	
		this.copyLinks = copyLinks;
	}

	/* (non-Javadoc)
     * @see sernet.verinice.iso27k.service.IProgressTask#run()
     */
	@Override
    public void run()  {
        if (this.elements.size() == 0) {
            log.debug("Can not copy elements because element list is empty.");
            return;
        }
        try {
            Activator.inheritVeriniceContextState();
            final List<String> uuidList = new ArrayList<String>(this.elements.size());
            for (final CnATreeElement element : this.elements) {
                uuidList.add(element.getUuid());
            }
            numberOfElements = uuidList.size();
            // -1 means unknown runtime
            progressObserver.beginTask(Messages.getString("CopyService.1", numberOfElements), -1); //$NON-NLS-1$
            if (isModelingTemplateActive()) {
                sernet.verinice.service.commands.templates.CopyCommand cc = new sernet.verinice.service.commands.templates.CopyCommand(this.selectedGroup.getUuid(), uuidList, getPostProcessorList(), this.copyLinks);
                cc.setCopyAttachments(isCopyAttachments());
                cc = getCommandService().executeCommand(cc);
                numberOfElements = cc.getNumber();
                newElements = cc.getNewElements();
            } else {
                sernet.verinice.service.commands.CopyCommand cc = new sernet.verinice.service.commands.CopyCommand(this.selectedGroup.getUuid(), uuidList, getPostProcessorList(), this.copyLinks);
                cc.setCopyAttachments(isCopyAttachments());
                cc = getCommandService().executeCommand(cc);
                numberOfElements = cc.getNumber();
                newElements = cc.getNewElements();
            }
            progressObserver.setTaskName(Messages.getString("CopyService.4")); //$NON-NLS-1$
            CnAElementFactory.getInstance().reloadModelFromDatabase();
        } catch (final Exception e) {
            log.error("Error while copying element", e); //$NON-NLS-1$
            throw new RuntimeException("Error while copying element", e); //$NON-NLS-1$
        } finally {
            progressObserver.done();
        }
    }

    public List<String> getNewElements() {
        return newElements;
    }

    /**
     * @return the copyAttachments
     */
    public boolean isCopyAttachments() {
        return copyAttachments;
    }

    /**
     * @param copyAttachments the copyAttachments to set
     */
    public void setCopyAttachments(final boolean copyAttachments) {
        this.copyAttachments = copyAttachments;
    }
	
    private boolean isModelingTemplateActive() {
        boolean standalone = Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.OPERATION_MODE).equals(PreferenceConstants.OPERATION_MODE_INTERNAL_SERVER);
        if (standalone) {
            return false;
        }
        LoadModelingTemplateSettings modelingTemplateSettings = new LoadModelingTemplateSettings();
        try {
            modelingTemplateSettings = getCommandService().executeCommand(modelingTemplateSettings);
        } catch (CommandException e) {
            log.error("Error while loading modeling template settings", e); //$NON-NLS-1$
        }
        return modelingTemplateSettings.isModelingTemplateActive();
    }
}
