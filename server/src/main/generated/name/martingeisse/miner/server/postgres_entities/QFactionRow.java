package name.martingeisse.miner.server.postgres_entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFactionRow is a Querydsl query type for FactionRow
 */
@Generated("name.martingeisse.miner.server.tools.codegen.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QFactionRow extends com.querydsl.sql.RelationalPathBase<FactionRow> {

    private static final long serialVersionUID = -474695477;

    public static final QFactionRow Faction = new QFactionRow("Faction");

    public final NumberPath<Long> divinePower = createNumber("divinePower", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> score = createNumber("score", Long.class);

    public final com.querydsl.sql.PrimaryKey<FactionRow> factionPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<PlayerRow> _playerFactionIdFkey = createInvForeignKey(id, "factionId");

    public QFactionRow(String variable) {
        super(FactionRow.class, forVariable(variable), "miner", "Faction");
        addMetadata();
    }

    public QFactionRow(String variable, String schema, String table) {
        super(FactionRow.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFactionRow(Path<? extends FactionRow> path) {
        super(path.getType(), path.getMetadata(), "miner", "Faction");
        addMetadata();
    }

    public QFactionRow(PathMetadata metadata) {
        super(FactionRow.class, metadata, "miner", "Faction");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(divinePower, ColumnMetadata.named("divinePower").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(score, ColumnMetadata.named("score").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

