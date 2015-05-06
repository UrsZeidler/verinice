package sernet.verinice.search;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;

/*******************************************************************************
 * Copyright (c) 2015 Daniel Murygin.
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

/**
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class ElasticsearchClientFactory implements DisposableBean {

    private Node node = null;
    private Client client = null;
    private Settings settings = null;
    
    /*
     * location of ES-working directory, injected by spring, different in Tier2 and Tier3
     */
    private Resource indexLocation;
    
    private final static Logger LOG = Logger.getLogger(ElasticsearchClientFactory.class);

    public void init() {
        if (node == null || node.isClosed()) {
            // Build and start the node
            node = NodeBuilder.nodeBuilder().settings(buildNodeSettings()).node();
            if(LOG.isDebugEnabled()){
                for(Entry<String, String> entry : node.settings().getAsMap().entrySet()){
                    LOG.debug("nodeEntry:\t <" + entry.getKey() + ", " + entry.getValue() + ">");
                }
            }
            // Get a client
            client = node.client();
            configure();
            // Wait for Yellow status
            client
                .admin()
                .cluster()
                .prepareHealth()
                .setWaitForYellowStatus()
                .setTimeout(TimeValue.timeValueMinutes(1))
                .execute()
                .actionGet();
        }
    }

    private void configure() {
        if(!client.admin().indices().prepareExists(ISearchDao.INDEX_NAME).execute().actionGet().isExists()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating index " + ISearchDao.INDEX_NAME + "...");
            }              
            client.admin().indices().prepareCreate(ISearchDao.INDEX_NAME)
            .setSettings(getAnylysisConf())
            .addMapping(ElementDao.TYPE_NAME, getMapping())
            .execute().actionGet();
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("Index " + ISearchDao.INDEX_NAME + " exists");
        } 
    }

    private Builder getAnylysisConf() {
        return ImmutableSettings.settingsBuilder().loadFromClasspath("sernet/verinice/search/analysis.json");
    }

    private String getMapping() {
        InputStream in = this.getClass().getResourceAsStream("/sernet/verinice/search/mapping.json");
        String mapping = null;
        try {
            mapping = IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            LOG.error("Error while reading mapping file", e);
        }
        return mapping;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (node != null && !node.isClosed()) {
            node.close();
        }
    }

    public Client getClient() {
        return client;
    }

    protected Settings buildNodeSettings() {
        try {
        // Build settings
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
            .put("node.name", "elasticsearch-" + NetworkUtils.getLocalAddress().getHostName())
            .put("node.data", true)
            .put("cluster.name", "elasticsearch-cluster-" + NetworkUtils.getLocalAddress().getHostName())
            .put("index.store.type", "niofs")
            //.put("index.store.fs.memory.enabled", "true")
            .put("gateway.type", "local")
            .put("path.data", getIndexLocation().getFile().getAbsolutePath() + "/data")
            .put("path.work", getIndexLocation().getFile().getAbsolutePath() + "/work")
            .put("path.logs", getIndexLocation().getFile().getAbsolutePath() + "/logs")
            .put("node.local", true);
        if (settings != null) {
            builder.put(settings);
            if(LOG.isDebugEnabled()){
                if(builder.internalMap() == null ||  builder.internalMap().size() == 0){
                    LOG.debug("nothing is on the builder map");
                }
                for(Entry<String, String> entry : builder.internalMap().entrySet()){
                    LOG.debug("<" + entry.getKey() + ", " + entry.getValue() + ">");
                }
            }
        }
        return builder.build();
        } catch (IOException e) {
            LOG.error("Error setting up ES-Client-Factory", e);
        }
        return null;
    }

    /**
     * @return the indexLocation
     */
    public Resource getIndexLocation() {
        return indexLocation;
    }

    /**
     * @param indexLocation the indexLocation to set
     */
    public void setIndexLocation(Resource indexLocation) {
        this.indexLocation = indexLocation;
    }

}
