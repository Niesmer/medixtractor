package AHA.medixtractor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcArrayColumns;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.relational.core.dialect.AnsiDialect;

@Configuration
public class SqliteJdbcDialectConfig {

    @Bean
    public JdbcDialect jdbcDialect() {
        return SqliteJdbcDialect.INSTANCE;
    }

    static final class SqliteJdbcDialect extends AnsiDialect implements JdbcDialect {
        static final SqliteJdbcDialect INSTANCE = new SqliteJdbcDialect();

        private SqliteJdbcDialect() {
        }

        @Override
        public JdbcArrayColumns getArraySupport() {
            return JdbcArrayColumns.Unsupported.INSTANCE;
        }
    }
}