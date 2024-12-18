package org.bitebuilders.config;

import org.bitebuilders.converter.condition.ConditionToStringConverter;
import org.bitebuilders.converter.condition.StringToConditionConverter;
import org.bitebuilders.converter.messageStatus.MessageStatusToStringConverter;
import org.bitebuilders.converter.messageStatus.StringToMessageStatusConverter;
import org.bitebuilders.converter.statusRequest.StatusRequestToStringConverter;
import org.bitebuilders.converter.statusRequest.StringToStatusRequestConverter;
import org.bitebuilders.converter.time.OffsetDateTimeReadConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.Arrays;

@Configuration
@EnableJdbcRepositories(basePackages = "org.bitebuilders.repository")
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new ConditionToStringConverter(),
                new StringToConditionConverter(),
                // converting from type [java.sql.Timestamp] to type [java.time.OffsetDateTime]]
                new OffsetDateTimeReadConverter(),
                new StringToStatusRequestConverter(),
                new StatusRequestToStringConverter(),
                new StringToMessageStatusConverter(),
                new MessageStatusToStringConverter()
        ));
    }
}

