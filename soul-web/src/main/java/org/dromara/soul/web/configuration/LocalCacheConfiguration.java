/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.dromara.soul.web.configuration;

import org.I0Itec.zkclient.ZkClient;
import org.dromara.soul.common.dto.AppAuthData;
import org.dromara.soul.common.dto.PluginData;
import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.dto.SelectorData;
import org.dromara.soul.configuration.zookeeper.ZookeeperConfiguration;
import org.dromara.soul.web.cache.HttpLongPollSyncCache;
import org.dromara.soul.web.cache.LocalCacheManager;
import org.dromara.soul.web.cache.WebsocketSyncCache;
import org.dromara.soul.web.cache.ZookeeperSyncCache;
import org.dromara.soul.web.config.SoulConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Automatic data cache configuration for caching {@link AppAuthData}、{@link PluginData}、{@link RuleData}、{@link SelectorData}
 *
 * @author huangxiaofeng
 * @author xiaoyu
 */
@AutoConfigureBefore(SoulConfiguration.class)
public class LocalCacheConfiguration {

    /**
     * The type Zookeeper.
     */
    @Configuration
    @ConditionalOnMissingBean(LocalCacheManager.class)
    @ConditionalOnProperty(name = "soul.sync.strategy", havingValue = "zookeeper")
    @Import(ZookeeperConfiguration.class)
    static class ZookeeperCacheManager {

        /**
         * Zookeeper cache manager local cache manager.
         *
         * @param zkClient the zk client
         * @return the local cache manager
         */
        @Bean
        public LocalCacheManager localCacheManager(ZkClient zkClient) {
            return new ZookeeperSyncCache(zkClient);
        }
    }


    /**
     * The type Http cache manager.
     */
    @Configuration
    @ConditionalOnMissingBean(LocalCacheManager.class)
    @ConditionalOnProperty(name = "soul.sync.strategy", havingValue = "http", matchIfMissing = true)
    static class HttpCacheManager {

        /**
         * Http config http config.
         *
         * @return the http config
         */
        @ConfigurationProperties(prefix = "soul.sync.http")
        @Bean
        public SoulConfig.HttpConfig httpConfig() {
            return new SoulConfig.HttpConfig();
        }

        /**
         * Config event listener local cache manager.
         *
         * @param httpConfig the http config
         * @return the local cache manager
         */
        @Bean
        public LocalCacheManager localCacheManager(final SoulConfig.HttpConfig httpConfig) {
            return new HttpLongPollSyncCache(httpConfig);
        }

    }

    /**
     * The type Websocket cache manager.
     */
    @Configuration
    @ConditionalOnMissingBean(LocalCacheManager.class)
    @ConditionalOnProperty(name = "soul.sync.strategy", havingValue = "websocket")
    static class WebsocketCacheManager {

        /**
         * Http config websocket config.
         *
         * @return the websocket config
         */
        @ConfigurationProperties(prefix = "soul.sync.websocket")
        @Bean
        public SoulConfig.WebsocketConfig httpConfig() {
            return new SoulConfig.WebsocketConfig();
        }

        /**
         * Config event listener local cache manager.
         *
         * @param websocketConfig the websocket config
         * @return the local cache manager
         */
        @Bean
        public LocalCacheManager localCacheManager(final SoulConfig.WebsocketConfig websocketConfig) {
            return new WebsocketSyncCache(websocketConfig);
        }

    }

}
