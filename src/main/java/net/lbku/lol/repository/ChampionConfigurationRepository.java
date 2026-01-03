package net.lbku.lol.repository;

import net.lbku.lol.model.ChampionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public final class ChampionConfigurationRepository {
    private final DynamoDbTable<ChampionConfiguration> configurations;

    @Autowired
    public ChampionConfigurationRepository(
        DynamoDbEnhancedClient dynamoDbClient,
        @Value("${app.aws.dynamodb.tables.champion-configurations}") String tableName
    ) {
        TableSchema<ChampionConfiguration> tableSchema = TableSchema.fromImmutableClass(
            ChampionConfiguration.class
        );

        this.configurations = dynamoDbClient.table(tableName, tableSchema);
    }

    public Iterable<ChampionConfiguration> findAll() {
        return this.configurations.scan()
                                  .items()
                                  .stream()
                                  .toList();
    }
}
