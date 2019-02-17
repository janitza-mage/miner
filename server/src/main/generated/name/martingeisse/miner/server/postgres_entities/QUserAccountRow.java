package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QUserAccountRow is a Querydsl query type for UserAccountRow
 */
@Generated("name.martingeisse.miner.server.tools.codegen.MyMetaDataSerializer")
@SuppressWarnings("all")
public class QUserAccountRow extends com.querydsl.sql.RelationalPathBase<UserAccountRow> {

	private static final long serialVersionUID = 1866574533;

	public static final QUserAccountRow UserAccount = new QUserAccountRow("UserAccount");

	public final BooleanPath deleted = createBoolean("deleted");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath passwordHash = createString("passwordHash");

	public final StringPath username = createString("username");

	public final com.querydsl.sql.PrimaryKey<UserAccountRow> userAccountPkey = createPrimaryKey(id);

	public final com.querydsl.sql.ForeignKey<PlayerRow> _playerUserAccountIdFkey = createInvForeignKey(id, "userAccountId");

	public QUserAccountRow(String variable) {
		super(UserAccountRow.class, forVariable(variable), "miner", "UserAccount");
		addMetadata();
	}

	public QUserAccountRow(String variable, String schema, String table) {
		super(UserAccountRow.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QUserAccountRow(Path<? extends UserAccountRow> path) {
		super(path.getType(), path.getMetadata(), "miner", "UserAccount");
		addMetadata();
	}

	public QUserAccountRow(PathMetadata metadata) {
		super(UserAccountRow.class, metadata, "miner", "UserAccount");
		addMetadata();
	}

	public void addMetadata() {
		addMetadata(deleted, ColumnMetadata.named("deleted").withIndex(4).ofType(Types.BIT).withSize(1).notNull());
		addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
		addMetadata(passwordHash, ColumnMetadata.named("passwordHash").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
		addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
	}

}

