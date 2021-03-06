/*******************************************************************************
 * Copyright (c) 2013 Daniel Murygin.
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.interfaces.graph;

import java.io.Serializable;

import sernet.verinice.interfaces.GraphCommand;
import sernet.verinice.model.common.CnATreeElement;

/**
 * IElementFilter filter elements in {@link IGraphElementLoader}s
 * of {@link GraphCommand}s.
 *
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public interface IElementFilter extends Serializable {

    /**
     * @param element
     * @return
     */
    boolean check(CnATreeElement element);

}
