package name.martingeisse.miner.server.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserAccount is a Querydsl query type for UserAccount
 */
@Generated("de.servicereisen.companion.tools.sql.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QUserAccount extends com.querydsl.sql.RelationalPathBase<UserAccount> {

    private static final long serialVersionUID = -2118534292;

    public static final QUserAccount UserAccount = new QUserAccount("UserAccount");

    public final BooleanPath deleted = createBoolean("deleted");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath passwordHash = createString("passwordHash");

    public final StringPath username = createString("username");

    public final com.querydsl.sql.PrimaryKey<UserAccount> userAccountPkey = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<Player> _playerUserAccountIdFkey = createInvForeignKey(id, "userAccountId");

    public QUserAccount(String variable) {
        super(UserAccount.class, forVariable(variable), "miner", "UserAccount");
        addMetadata();
    }

    public QUserAccount(String variable, String schema, String table) {
        super(UserAccount.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserAccount(Path<? extends UserAccount> path) {
        super(path.getType(), path.getMetadata(), "miner", "UserAccount");
        addMetadata();
    }

    public QUserAccount(PathMetadata metadata) {
        super(UserAccount.class, metadata, "miner", "UserAccount");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(deleted, ColumnMetadata.named("deleted").withIndex(4).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(passwordHash, ColumnMetadata.named("passwordHash").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

