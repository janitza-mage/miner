package name.martingeisse.miner.server.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPlayer is a Querydsl query type for Player
 */
@Generated("de.servicereisen.companion.tools.sql.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QPlayer extends com.querydsl.sql.RelationalPathBase<Player> {

    private static final long serialVersionUID = -162185961;

    public static final QPlayer Player = new QPlayer("Player");

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

    public final com.querydsl.sql.PrimaryKey<Player> playerPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<UserAccount> playerUserAccountIdFkey = createForeignKey(userAccountId, "id");

    public final com.querydsl.sql.ForeignKey<Faction> playerFactionIdFkey = createForeignKey(factionId, "id");

    public final com.querydsl.sql.ForeignKey<PlayerInventorySlot> _playerInventorySlotPlayerIdFkey = createInvForeignKey(id, "playerId");

    public final com.querydsl.sql.ForeignKey<PlayerAwardedAchievement> _playerAwardedAchievementPlayerIdFkey = createInvForeignKey(id, "playerId");

    public QPlayer(String variable) {
        super(Player.class, forVariable(variable), "miner", "Player");
        addMetadata();
    }

    public QPlayer(String variable, String schema, String table) {
        super(Player.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPlayer(Path<? extends Player> path) {
        super(path.getType(), path.getMetadata(), "miner", "Player");
        addMetadata();
    }

    public QPlayer(PathMetadata metadata) {
        super(Player.class, metadata, "miner", "Player");
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

