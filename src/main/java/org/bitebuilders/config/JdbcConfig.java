package org.bitebuilders.config;

import org.bitebuilders.converter.time.OffsetDateTimeReadConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new ConditionToPostgresConverter(),
//                new ConditionToStringConverter(),
//                new StringToConditionConverter(),
                new OffsetDateTimeReadConverter()
//                new OffsetDateTimeWriteConverter()
        ));
    }
}

