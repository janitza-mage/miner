package name.martingeisse.miner.server.postgres_entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPlayerRow is a Querydsl query type for PlayerRow
 */
@Generated("name.martingeisse.miner.server.tools.codegen.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QPlayerRow extends com.querydsl.sql.RelationalPathBase<PlayerRow> {

    private static final long serialVersionUID = 692489964;

    public static final QPlayerRow Player = new QPlayerRow("Player");

    public final NumberPath<Long> coins = createNumber("coins", Long.class);

    public final BooleanPath deleted = createBoolean("deleted");

    public final NumberPath<Long> factionId = createNumber("factionId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> leftAngle = createNumber("leftAngle", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigDecimal> upAngle = createNumber("upAngle", java.math.BigDecimal.class);

    public final NumberPath<Long> userAccountId = createNumber("userAccountId", Long.class);

    public final NumberPath<java.math.BigDecimal> x = createNumber("x", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> y = createNumber("y", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> z = createNumber("z", java.math.BigDecimal.class);

    public final com.querydsl.sql.PrimaryKey<PlayerRow> playerPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<UserAccountRow> playerUserAccountIdFkey = createForeignKey(userAccountId, "id");

    public final com.querydsl.sql.ForeignKey<FactionRow> playerFactionIdFkey = createForeignKey(factionId, "id");

    public final com.querydsl.sql.ForeignKey<PlayerInventorySlotRow> _playerInventorySlotPlayerIdFkey = createInvForeignKey(id, "playerId");

    public final com.querydsl.sql.ForeignKey<PlayerAwardedAchievementRow> _playerAwardedAchievementPlayerIdFkey = createInvForeignKey(id, "playerId");

    public QPlayerRow(String variable) {
        super(PlayerRow.class, forVariable(variable), "miner", "Player");
        addMetadata();
    }

    public QPlayerRow(String variable, String schema, String table) {
        super(PlayerRow.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPlayerRow(Path<? extends PlayerRow> path) {
        super(path.getType(), path.getMetadata(), "miner", "Player");
        addMetadata();
    }

    public QPlayerRow(PathMetadata metadata) {
        super(PlayerRow.class, metadata, "miner", "Player");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(coins, ColumnMetadata.named("coins").withIndex(10).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(deleted, ColumnMetadata.named("deleted").withIndex(11).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(factionId, ColumnMetadata.named("factionId").withIndex(4).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(leftAngle, ColumnMetadata.named("leftAngle").withIndex(8).ofType(Types.NUMERIC).withSize(5).withDigits(2).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(upAngle, ColumnMetadata.named("upAngle").withIndex(9).ofType(Types.NUMERIC).withSize(5).withDigits(2).notNull());
        addMetadata(userAccountId, ColumnMetadata.named("userAccountId").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(x, ColumnMetadata.named("x").withIndex(5).ofType(Types.NUMERIC).withSize(10).withDigits(2).notNull());
        addMetadata(y, ColumnMetadata.named("y").withIndex(6).ofType(Types.NUMERIC).withSize(10).withDigits(2).notNull());
        addMetadata(z, ColumnMetadata.named("z").withIndex(7).ofType(Types.NUMERIC).withSize(10).withDigits(2).notNull());
    }

}

