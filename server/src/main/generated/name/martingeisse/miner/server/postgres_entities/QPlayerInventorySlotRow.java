package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;




/**
 * QPlayerInventorySlotRow is a Querydsl query type for PlayerInventorySlotRow
 */
@Generated("name.martingeisse.miner.server.tools.codegen.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QPlayerInventorySlotRow extends com.querydsl.sql.RelationalPathBase<PlayerInventorySlotRow> {

    private static final long serialVersionUID = 2106992174;

    public static final QPlayerInventorySlotRow PlayerInventorySlot = new QPlayerInventorySlotRow("PlayerInventorySlot");

    public final BooleanPath equipped = createBoolean("equipped");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> playerId = createNumber("playerId", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public final com.querydsl.sql.PrimaryKey<PlayerInventorySlotRow> playerInventorySlotPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<PlayerRow> playerInventorySlotPlayerIdFkey = createForeignKey(playerId, "id");

    public QPlayerInventorySlotRow(String variable) {
        super(PlayerInventorySlotRow.class, forVariable(variable), "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public QPlayerInventorySlotRow(String variable, String schema, String table) {
        super(PlayerInventorySlotRow.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPlayerInventorySlotRow(Path<? extends PlayerInventorySlotRow> path) {
        super(path.getType(), path.getMetadata(), "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public QPlayerInventorySlotRow(PathMetadata metadata) {
        super(PlayerInventorySlotRow.class, metadata, "miner", "PlayerInventorySlot");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(equipped, ColumnMetadata.named("equipped").withIndex(5).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(playerId, ColumnMetadata.named("playerId").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(quantity, ColumnMetadata.named("quantity").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(type, ColumnMetadata.named("type").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

