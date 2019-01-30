package name.martingeisse.miner.server.postgres_entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPlayerAwardedAchievementRow is a Querydsl query type for PlayerAwardedAchievementRow
 */
@Generated("name.martingeisse.miner.server.tools.codegen.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QPlayerAwardedAchievementRow extends com.querydsl.sql.RelationalPathBase<PlayerAwardedAchievementRow> {

    private static final long serialVersionUID = 1373313945;

    public static final QPlayerAwardedAchievementRow PlayerAwardedAchievement = new QPlayerAwardedAchievementRow("PlayerAwardedAchievement");

    public final StringPath achievementCode = createString("achievementCode");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> playerId = createNumber("playerId", Long.class);

    public final com.querydsl.sql.PrimaryKey<PlayerAwardedAchievementRow> playerAwardedAchievementPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<PlayerRow> playerAwardedAchievementPlayerIdFkey = createForeignKey(playerId, "id");

    public QPlayerAwardedAchievementRow(String variable) {
        super(PlayerAwardedAchievementRow.class, forVariable(variable), "miner", "PlayerAwardedAchievement");
        addMetadata();
    }

    public QPlayerAwardedAchievementRow(String variable, String schema, String table) {
        super(PlayerAwardedAchievementRow.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPlayerAwardedAchievementRow(Path<? extends PlayerAwardedAchievementRow> path) {
        super(path.getType(), path.getMetadata(), "miner", "PlayerAwardedAchievement");
        addMetadata();
    }

    public QPlayerAwardedAchievementRow(PathMetadata metadata) {
        super(PlayerAwardedAchievementRow.class, metadata, "miner", "PlayerAwardedAchievement");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(achievementCode, ColumnMetadata.named("achievementCode").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(playerId, ColumnMetadata.named("playerId").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

