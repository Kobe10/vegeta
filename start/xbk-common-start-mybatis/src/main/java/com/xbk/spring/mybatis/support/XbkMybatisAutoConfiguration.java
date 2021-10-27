package com.xbk.spring.mybatis.support;


import com.xbk.spring.datesource.common.Common;
import com.xbk.spring.datesource.config.druid.MultiDruidProperties;
import com.xbk.spring.datesource.core.dynamic.DynamicCommon;
import com.xbk.spring.datesource.core.dynamic.DynamicDataSource;
import com.xbk.spring.datesource.support.XbkDynamicAutoConfiguration;
import com.xbk.spring.mybatis.interceptor.ResultParamInterceptor;
import com.xbk.spring.mybatis.interceptor.SqlPageHelperInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer;
import tk.mybatis.mapper.autoconfigure.MapperAutoConfiguration;
import tk.mybatis.mapper.autoconfigure.MybatisProperties;
import tk.mybatis.mapper.autoconfigure.SpringBootVFS;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xbk.spring.mybatis.support.XbkMybatisAutoConfiguration.NAME;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnProperty(name = NAME, havingValue = XbkMybatisAutoConfiguration.NAME_DEFAULT_VALUE, matchIfMissing = true)
@EnableConfigurationProperties(value = {MultiDruidProperties.class, MybatisProperties.class})
@AutoConfigureAfter(value = {MultiDruidProperties.class, DataSource.class})
@Import(value = {XbkDynamicAutoConfiguration.class, XbkDynamicAutoConfiguration.DynamicAspectConfiguration.class})
public class XbkMybatisAutoConfiguration {

    /**
     * 默认开启该配置对应的名称
     */
    static final String NAME = "spring.datasource.mybatis.enable";

    /**
     * 默认开启该配置对应的名称 - 值
     */
    static final String NAME_DEFAULT_VALUE = "true";

    private final MybatisProperties properties;
    private final List<ConfigurationCustomizer> configurationCustomizers;

    @Autowired(required = false)
    private List<Interceptor> interceptorList;

    /**
     * 配置 sqlSessionFactory
     * {@link MapperAutoConfiguration}
     */
    @Primary
    @Bean(name = Common.MYBATIS_SQL_SESSION_FACTORY_NAME)
    @ConditionalOnBean(name = DynamicCommon.DYNAMIC_NAME)
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier(DynamicCommon.DYNAMIC_NAME) DynamicDataSource dynamicDataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setVfs(SpringBootVFS.class);
        sqlSessionFactory.setDataSource(dynamicDataSource);
        //设置扫描包信息
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        ArrayList<Resource> list = new ArrayList<>();
        list.addAll(Arrays.asList(patternResolver.getResources("classpath*:mybatis/mapper/*.xml")));
        list.addAll(Arrays.asList(patternResolver.getResources("classpath*:mybatis/mapper/**/*.xml")));
        list.addAll(Arrays.asList(patternResolver.getResources("classpath*:mybatis/mapper/**/**/*.xml")));
        sqlSessionFactory.setMapperLocations(list.stream().distinct().toArray(Resource[]::new));
        applyConfiguration(sqlSessionFactory);
        if (this.properties.getConfigurationProperties() != null) {
            sqlSessionFactory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (CollectionUtils.isEmpty(interceptorList)) {
            interceptorList = new ArrayList<>();
        }
        interceptorList.add(new ResultParamInterceptor());
        interceptorList.add(SqlPageHelperInterceptor.init());
        Interceptor[] interceptors = interceptorList.stream().toArray(Interceptor[]::new);
        sqlSessionFactory.setPlugins(interceptors);
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            sqlSessionFactory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            sqlSessionFactory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            sqlSessionFactory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        log.info(">>>>> 初始化【SqlSessionFactory】 完成");
        return sqlSessionFactory.getObject();
    }


    /**
     * 配置 SqlSessionTemplate
     */
    @Bean(name = Common.MYBATIS_SQL_SESSION_TEMPLATE_NAME)
    @ConditionalOnBean(name = Common.MYBATIS_SQL_SESSION_FACTORY_NAME)
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier(Common.MYBATIS_SQL_SESSION_FACTORY_NAME) SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        log.info(">>>>> 初始化【SqlSessionTemplate】 完成");
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * 配置事物管理器
     */
    @Bean(name = Common.MYBATIS_TRANSACTION_MANAGER_NAME)
    @ConditionalOnBean(name = DynamicCommon.DYNAMIC_NAME)
    @ConditionalOnMissingBean(name = Common.MYBATIS_TRANSACTION_MANAGER_NAME)
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(
            @Qualifier(DynamicCommon.DYNAMIC_NAME) DynamicDataSource dynamicDataSource) {
        log.info(">>>>> 初始化【DataSourceTransactionManager】 完成");
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    /**
     * 处理 sqlSessionFactory 配置信息
     */
    private void applyConfiguration(SqlSessionFactoryBean factory) {
        org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

}
