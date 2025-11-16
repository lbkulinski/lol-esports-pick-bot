package net.lbku.service;

import net.lbku.model.ChampionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Service
public final class ChampionConfigurationService {
    private final DynamoDbTable<ChampionConfiguration> configurations;

    @Autowired
    public ChampionConfigurationService(
        @Value("${app.aws.dynamodb.tables.champion-configurations}") String tableName,
        DynamoDbEnhancedClient dynamoDbClient
    ) {
        TableSchema<ChampionConfiguration> tableSchema = TableSchema.fromImmutableClass(
            ChampionConfiguration.class
        );

        this.configurations = dynamoDbClient.table(tableName, tableSchema);
    }

    public List<ChampionConfiguration> getConfigurations() {
        return this.configurations.scan()
                                  .items()
                                  .stream()
                                  .toList();
    }
}
