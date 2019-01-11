package name.martingeisse.miner.server.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPlayerInventorySlot is a Querydsl query type for PlayerInventorySlot
 */
@Generated("de.servicereisen.companion.tools.sql.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QPlayerInventorySlot extends com.querydsl.sql.RelationalPathBase<PlayerInventorySlot> {

    private static final long serialVersionUID = -1267361373;

    public static final QPlayerInventorySlot PlayerInventorySlot = new QPlayerInventorySlot("PlayerInventorySlot");

    public final BooleanPath equipped = createBoolean("equipped");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> index = createNumber("index", Integer.class);

    public final NumberPath<Long> playerId = createNumber("playerId", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public final com.querydsl.sql.PrimaryKey<PlayerInventorySlot> playerInventorySlotPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<Player> playerInventorySlotPlayerIdFkey = createForeignKey(playerId, "id");

    public QPlayerInventorySlot(String variable) {
        super(PlayerInventorySlot.class, forVariable(variable), "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public QPlayerInventorySlot(String variable, String schema, String table) {
        super(PlayerInventorySlot.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPlayerInventorySlot(Path<? extends PlayerInventorySlot> path) {
        super(path.getType(), path.getMetadata(), "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public QPlayerInventorySlot(PathMetadata metadata) {
        super(PlayerInventorySlot.class, metadata, "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(equipped, ColumnMetadata.named("equipped").withIndex(3).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(index, ColumnMetadata.named("index").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(playerId, ColumnMetadata.named("playerId").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(quantity, ColumnMetadata.named("quantity").withIndex(6).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(type, ColumnMetadata.named("type").withIndex(5).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

