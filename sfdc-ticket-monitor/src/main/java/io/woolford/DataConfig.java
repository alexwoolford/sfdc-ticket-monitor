package io.woolford;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.TaskScheduler;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan("io.woolford.database.mapper")
class DataConfig {

    @Value("${mysql.host}")
    private String mysqlHost;

    @Value("${mysql.port}")
    private String mysqlPort;

    @Value("${mysql.dbname}")
    private String mysqlDbname;

    @Value("${mysql.username}")
    private String mysqlUsername;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() throws SQLException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
        dataSource.setUrl("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDbname + "?useSSL=false");
        dataSource.setUsername(mysqlUsername);
        dataSource.setPassword(mysqlPassword);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory.getObject();
    }

    @Bean
    public TaskScheduler taskScheduler(LockProvider lockProvider) {
        int poolSize = 10;
        return SpringLockableTaskSchedulerFactory.newLockableTaskScheduler(poolSize, lockProvider);
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }


}