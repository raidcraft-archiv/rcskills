package de.raidcraft.skills.ebean;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Filter;
import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.JoinConfig;
import com.avaje.ebean.OrderBy;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.QueryListener;
import com.avaje.ebean.QueryResultVisitor;
import com.avaje.ebean.RawSql;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.ebean.Maintainable;

import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base-class for Ebean-mapped models.
 */
@MappedSuperclass
public class Model implements Maintainable {

    /**
     * Will either insert or update this entity depending on its state.
     */
    @Override
    public void save() {

        Ebean.save(this);
    }

    /**
     * Will either insert or update this entity depending on its state.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    @Override
    public void save(final Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).save(this);
    }

    /**
     * Updates this entity.
     */
    @Override
    public void update() {

        Ebean.update(this);
    }

    /**
     * Updates this entity.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    @Override
    public void update(final Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).update(this);
    }

    /**
     * Deletes this entity.
     */
    @Override
    public void delete() {

        Ebean.delete(this);
    }

    /**
     * Deletes this entity.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    @Override
    public void delete(final Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).delete(this);
    }

    /**
     * Refreshes this entity from the database.
     */
    @Override
    public void refresh() {

        Ebean.refresh(this);
    }

    /**
     * Refreshes this entity from the database.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    @Override
    public void refresh(final Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).refresh(this);
    }

    /**
     * Helper for Ebean queries
     *
     * @see <a href="http://www.avaje.org/static/javadoc/pub/">Ebean API documentation</a>
     */
    public static class Finder<I, T> implements Query<T> {

        private final Class<I> idType;
        private final Class<T> type;
        private final Class<? extends BasePlugin> sClazz;

        /**
         * Creates a finder for entity of type <code>T</code> with ID of type <code>I</code>, using a .. TODO: Add useful description
         *
         * @param idType ID type.
         * @param type   Entity type.
         * @param sClazz class, extends, registered - TODO: Add useful description
         */
        public Finder(final Class<I> idType, final Class<T> type, final Class<? extends BasePlugin> sClazz) {

            this.idType = idType;
            this.type = type;
            this.sClazz = sClazz;
        }

        private EbeanServer getServer() {

            return RaidCraft.getDatabase(this.sClazz);
        }

        /**
         * Changes the server.
         *
         * @param clazz class, extends, registered - TODO: Add useful description
         */
        @SuppressWarnings("unchecked")
        public Finder<I, T> onServer(final Class<? extends BasePlugin> clazz) {

            return new Finder(this.idType, this.type, clazz);
        }

        /**
         * Retrivies all entities of the given type.
         */
        public List<T> allEntities() {

            return this.getServer().find(this.type).findList();
        }

        /**
         * Retrieves an entity by ID.
         */
        public T byId(final I id) {

            return this.getServer().find(this.type, id);
        }

        /**
         * Retrieves an entity reference by ID.
         */
        public T reference(final I id) {

            return this.getServer().getReference(this.type, id);
        }

        /**
         * Returns the next identity value.
         */
        @SuppressWarnings("unchecked")
        public I nextId() {

            return (I) this.getServer().nextId(this.type);
        }

        /**
         * Creates a filter for sorting and filtering lists of entities locally without going back to the database.
         */
        public Filter<T> filter() {

            return this.getServer().filter(this.type);
        }

        /**
         * Creates a query.
         */
        public Query<T> query() {

            return this.getServer().find(this.type);
        }

        /**
         * Cancels query execution, if supported by the underlying database and driver.
         */
        @Override
        public void cancel() {

            this.query().cancel();
        }

        /**
         * Copies this query.
         */
        @Override
        public Query<T> copy() {

            return this.query().copy();
        }

        /**
         * Specifies a path to load including all its properties.
         */
        @Override
        public Query<T> fetch(final String path) {

            return this.query().fetch(path);
        }

        /**
         * Specifies a path to fetch with a specific list properties to include, to load a partial object.
         */
        @Override
        public Query<T> fetch(final String path, final String fetchProperties) {

            return this.query().fetch(path, fetchProperties);
        }

        /**
         * Additionally specifies a <code>FetchConfig</code> to specify a 'query join' and/or define the lazy loading query.
         */
        @Override
        public Query<T> fetch(final String path, final FetchConfig fetchConfig) {

            return this.query().fetch(path, fetchConfig);
        }

        /**
         * Additionally specifies a <code>FetchConfig</code> to use a separate query or lazy loading to load this path.
         */
        @Override
        public Query<T> fetch(final String assocProperty, final String fetchProperties, final FetchConfig fetchConfig) {

            return this.query().fetch(assocProperty, fetchProperties, fetchConfig);
        }

        /**
         * USE: {@link com.avaje.ebean.Query#fetch(String)}
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public Query<T> join(final String path) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: {@link com.avaje.ebean.Query#fetch(String, String)})}
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public Query<T> join(final String path, final String joinProperties) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: {@link com.avaje.ebean.Query#fetch(String, com.avaje.ebean.FetchConfig)})}
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public Query<T> join(final String path, final JoinConfig joinConfig) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: {@link com.avaje.ebean.Query#fetch(String, String, com.avaje.ebean.FetchConfig)})}
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public Query<T> join(final String assocProperty, final String joinProperties, final JoinConfig joinConfig) {

            throw new UnsupportedOperationException();
        }

        /**
         * Executes a find IDs query in a background thread.
         */
        @Override
        public FutureIds<T> findFutureIds() {

            return this.query().findFutureIds();
        }

        /**
         * Executes a find list query in a background thread.
         */
        @Override
        public FutureList<T> findFutureList() {

            return this.query().findFutureList();
        }

        /**
         * Executes a find row count query in a background thread.
         */
        @Override
        public FutureRowCount<T> findFutureRowCount() {

            return this.query().findFutureRowCount();
        }

        /**
         * Executes a query and returns the result as a list of IDs.
         */
        @Override
        public List<Object> findIds() {

            return this.query().findIds();
        }

        /**
         * Executes the query and returns the results as a list of objects.
         */
        @Override
        public List<T> findList() {

            return this.query().findList();
        }

        /**
         * Executes the query and returns the results as a map of objects.
         */
        @Override
        public Map<?, T> findMap() {

            return this.query().findMap();
        }

        /**
         * Executes the query and returns the results as a map of the objects.
         */
        @Override
        public <K> Map<K, T> findMap(final String a, final Class<K> kClass) {

            return this.query().findMap(a, kClass);
        }

        /**
         * Returns a <code>PagingList</code> for this query.
         */
        @Override
        public PagingList<T> findPagingList(final int pageSize) {

            return this.query().findPagingList(pageSize);
        }

        /**
         * Returns the number of entities this query should return.
         */
        @Override
        public int findRowCount() {

            return this.query().findRowCount();
        }

        /**
         * Executes the query and returns the results as a set of objects.
         */
        @Override
        public Set<T> findSet() {

            return this.query().findSet();
        }

        /**
         * Executes the query and returns the results as either a single bean or <code>null</code>, if no matching bean is found.
         */
        @Override
        public T findUnique() {

            return this.query().findUnique();
        }

        /**
         * Executes a <code>QueryResultVisitor</code> for this query.
         */
        @Override
        public void findVisit(final QueryResultVisitor<T> visitor) {

            this.query().findVisit(visitor);
        }

        /**
         * Returns the <code>QueryIterator</code> for this query.
         */
        @Override
        public QueryIterator<T> findIterate() {

            return this.query().findIterate();
        }

        /**
         * Returns the <code>ExpressionFactory</code> used by this query.
         */
        @Override
        public ExpressionFactory getExpressionFactory() {

            return this.query().getExpressionFactory();
        }

        /**
         * Returns the first row value.
         */
        @Override
        public int getFirstRow() {

            return this.query().getFirstRow();
        }

        /**
         * Returns the SQL that was generated for executing this query.
         */
        @Override
        public String getGeneratedSql() {

            return this.query().getGeneratedSql();
        }

        /**
         * Returns the maximum of rows for this query.
         */
        @Override
        public int getMaxRows() {

            return this.query().getMaxRows();
        }

        /**
         * Sets the index value to query.
         */
        @Override
        public Query<T> setUseIndex(final UseIndex useIndex) {

            return this.query().setUseIndex(useIndex);
        }

        /**
         * Gets the index value for this query.
         */
        @Override
        public UseIndex getUseIndex() {

            return this.query().getUseIndex();
        }

        /**
         * Returns the type of query.
         */
        @Override
        public Type getType() {

            return this.query().getType();
        }

        /**
         * Returns the <code>RawSql</code> that was set to use for this query.
         */
        @Override
        public RawSql getRawSql() {

            return this.query().getRawSql();
        }

        /**
         * Returns the query's <code>having</code> clause.
         */
        @Override
        public ExpressionList<T> having() {

            return this.query().having();
        }

        /**
         * Adds an expression to the <code>having</code> clause and returns the query.
         */
        @Override
        public Query<T> having(final Expression expressionToAdd) {

            return this.query().having(expressionToAdd);
        }

        /**
         * Adds clauses to the <code>having</code> clause and returns the query.
         */
        @Override
        public Query<T> having(final String addToHavingClause) {

            return this.query().having(addToHavingClause);
        }

        /**
         * Returns <code>true</code> if this query was tuned by <code>autoFetch</code>.
         */
        @Override
        public boolean isAutofetchTuned() {

            return this.query().isAutofetchTuned();
        }

        /**
         * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
         * <p>
         * This is exactly the same as {@link #orderBy}.
         */
        @Override
        public OrderBy<T> order() {

            return this.query().order();
        }

        /**
         * Sets the <code>order by</code> clause, replacing the existing <code>order by</code> clause if there is one.
         * <p>
         * This is exactly the same as {@link #orderBy(String)}.
         */
        @Override
        public Query<T> order(final String orderByClause) {

            return this.query().order(orderByClause);
        }

        /**
         * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
         * <p>
         * This is exactly the same as {@link #order}.
         */
        @Override
        public OrderBy<T> orderBy() {

            return this.query().orderBy();
        }

        /**
         * Set the <code>order by</code> clause replacing the existing <code>order by</code> clause if there is one.
         * <p>
         * This is exactly the same as {@link #order(String)}.
         */
        @Override
        public Query<T> orderBy(final String orderByClause) {

            return this.query().orderBy(orderByClause);
        }

        /**
         * Explicitly sets a comma delimited list of the properties to fetch on the 'main' entity bean, to load a partial object.
         */
        @Override
        public Query<T> select(final String fetchProperties) {

            return this.query().select(fetchProperties);
        }

        /**
         * Explicitly specifies whether to use 'Autofetch' for this query.
         */
        @Override
        public Query<T> setAutofetch(final boolean autofetch) {

            return this.query().setAutofetch(autofetch);
        }

        /**
         * Sets the rows after which fetching should continue in a background thread.
         */
        @Override
        public Query<T> setBackgroundFetchAfter(final int backgroundFetchAfter) {

            return this.query().setBackgroundFetchAfter(backgroundFetchAfter);
        }

        /**
         * Sets a hint, which for JDBC translates to <code>Statement.fetchSize()</code>.
         */
        @Override
        public Query<T> setBufferFetchSizeHint(final int fetchSize) {

            return this.query().setBufferFetchSizeHint(fetchSize);
        }

        /**
         * Sets whether this query uses <code>DISTINCT</code>.
         */
        @Override
        public Query<T> setDistinct(final boolean isDistinct) {

            return this.query().setDistinct(isDistinct);
        }

        /**
         * Set this to true and the beans and collections returned will be plain classes rather than Ebean generated dynamic subclasses etc.
         */
        @Override
        public Query<T> setVanillaMode(final boolean isVanillaMode) {

            return this.query().setVanillaMode(isVanillaMode);
        }

        /**
         * Sets the first row to return for this query.
         */
        @Override
        public Query<T> setFirstRow(final int firstRow) {

            return this.query().setFirstRow(firstRow);
        }

        /**
         * Sets the ID value to query.
         */
        @Override
        public Query<T> setId(final Object id) {

            return this.query().setId(id);
        }

        /**
         * Sets a listener to process the query on a row-by-row basis.
         */
        @Override
        public Query<T> setListener(final QueryListener<T> queryListener) {

            return this.query().setListener(queryListener);
        }

        /**
         * When set to <code>true</code>, all the beans from this query are loaded into the bean cache.
         */
        @Override
        public Query<T> setLoadBeanCache(final boolean loadBeanCache) {

            return this.query().setLoadBeanCache(loadBeanCache);
        }

        /**
         * Sets the property to use as keys for a map.
         */
        @Override
        public Query<T> setMapKey(final String mapKey) {

            return this.query().setMapKey(mapKey);
        }

        /**
         * Sets the maximum number of rows to return in the query.
         */
        @Override
        public Query<T> setMaxRows(final int maxRows) {

            return this.query().setMaxRows(maxRows);
        }

        /**
         * Replaces any existing <code>order by</code> clause using an <code>OrderBy</code> object.
         * <p>
         * This is exactly the same as {@link #setOrderBy(com.avaje.ebean.OrderBy)}.
         */
        @Override
        public Query<T> setOrder(final OrderBy<T> orderBy) {

            return this.query().setOrder(orderBy);
        }

        /**
         * Set an OrderBy object to replace any existing <code>order by</code> clause.
         * <p>
         * This is exactly the same as {@link #setOrder(com.avaje.ebean.OrderBy)}.
         */
        @Override
        public Query<T> setOrderBy(final OrderBy<T> orderBy) {

            return this.query().setOrderBy(orderBy);
        }

        /**
         * Sets an ordered bind parameter according to its position.
         */
        @Override
        public Query<T> setParameter(final int position, final Object value) {

            return this.query().setParameter(position, value);
        }

        /**
         * Sets a named bind parameter.
         */
        @Override
        public Query<T> setParameter(final String name, final Object value) {

            return this.query().setParameter(name, value);
        }

        /**
         * Sets the OQL query to run
         */
        @Override
        public Query<T> setQuery(final String oql) {

            return this.getServer().createQuery(type, oql);
        }

        /**
         * Sets <code>RawSql</code> to use for this query.
         */
        @Override
        public Query<T> setRawSql(final RawSql rawSql) {

            return this.query().setRawSql(rawSql);
        }

        /**
         * Sets whether the returned beans will be read-only.
         */
        @Override
        public Query<T> setReadOnly(final boolean readOnly) {

            return this.query().setReadOnly(readOnly);
        }

        /**
         * Sets a timeout on this query.
         */
        public Query<T> setTimeout(int secs) {

            return this.query().setTimeout(secs);
        }

        /**
         * Sets whether to use the bean cache.
         */
        @Override
        public Query<T> setUseCache(final boolean useBeanCache) {

            return this.query().setUseCache(useBeanCache);
        }

        /**
         * Sets whether to use the query cache.
         */
        @Override
        public Query<T> setUseQueryCache(final boolean useQueryCache) {

            return this.query().setUseQueryCache(useQueryCache);
        }

        /**
         * Adds expressions to the <code>where</code> clause with the ability to chain on the <code>ExpressionList</code>.
         */
        @Override
        public ExpressionList<T> where() {

            return this.query().where();
        }

        /**
         * Applies a filter on the 'many' property list rather than the root level objects.
         */
        @Override
        public ExpressionList<T> filterMany(final String propertyName) {

            return this.query().filterMany(propertyName);
        }

        /**
         * Adds a single <code>Expression</code> to the <code>where</code> clause and returns the query.
         */
        @Override
        public Query<T> where(final Expression expression) {

            return this.query().where(expression);
        }

        /**
         * Adds additional clauses to the <code>where</code> clause.
         */
        @Override
        public Query<T> where(final String addToWhereClause) {

            return this.query().where(addToWhereClause);
        }

        /**
         * A string representation of this object.
         *
         * Only for debugging.
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder();
            final String newLine = System.getProperty("line.separator");

            result.append(this.getClass().getName()).append(" Object {").append(newLine);
            result.append(" idType: ").append(idType).append(newLine);
            result.append(" type: ").append(type).append(newLine);
            result.append(" sClazz: ").append(sClazz).append(newLine);
            result.append("}");

            return result.toString();
        }
    }
}