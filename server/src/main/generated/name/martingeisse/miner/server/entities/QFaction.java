package name.martingeisse.miner.server.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFaction is a Querydsl query type for Faction
 */
@Generated("de.servicereisen.companion.tools.sql.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QFaction extends com.querydsl.sql.RelationalPathBase<Faction> {

    private static final long serialVersionUID = -1331118426;

    public static final QFaction Faction = new QFaction("Faction");

    public final NumberPath<Long> divinePower = createNumber("divinePower", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> score = createNumber("score", Long.class);

    public final com.querydsl.sql.PrimaryKey<Faction> factionPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<Player> _playerFactionIdFkey = createInvForeignKey(id, "factionId");

    public QFaction(String variable) {
        super(Faction.class, forVariable(variable), "miner", "Faction");
        addMetadata();
    }

    public QFaction(String variable, String schema, String table) {
        super(Faction.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFaction(Path<? extends Faction> path) {
        super(path.getType(), path.getMetadata(), "miner", "Faction");
        addMetadata();
    }

    public QFaction(PathMetadata metadata) {
        super(Faction.class, metadata, "miner", "Faction");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(divinePower, ColumnMetadata.named("divinePower").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(score, ColumnMetadata.named("score").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

