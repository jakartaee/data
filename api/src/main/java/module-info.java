/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Limit;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Sort;

/**
 * <p>Jakarta Data standardizes a programming model where data is represented by simple Java classes
 * and where operations on data are represented by interface methods.</p>
 *
 * <p>The application defines simple Java objects called entities to represent data in the database.
 * Fields or accessor methods designate each entity property. For example,</p>
 *
 * <pre>
 * &#64;Entity
 * public class Product {
 *     &#64;Id
 *     public long id;
 *     public String name;
 *     public float price;
 *     public int yearProduced;
 *     ...
 * }
 * </pre>
 *
 * <p>The application defines interface methods on separate classes called repositories
 * to perform queries and other operations on entities. Repositories are interface classes
 * that are annotated with the {@link Repository} annotation. For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface Products extends CrudRepository&lt;Product, Long&gt; {
 *
 *     &#64;OrderBy("price")
 *     List&lt;Product&gt; findByNameIgnoreCaseLikeAndPriceLessThan(String namePattern, float max);
 *
 *     &#64;Query("UPDATE Product o SET o.price = o.price * (1.0 - ?1) WHERE o.yearProduced &lt;= ?2")
 *     int discountOldInventory(float rateOfDiscount, int maxYear);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>Repository interfaces are implemented by the container/runtime and are made available
 * to applications via the <code>jakarta.inject.Inject</code> annotation.
 * For example,</p>
 *
 * <pre>
 * &#64;Inject
 * Products products;
 *
 * ...
 * found = products.findByNameIgnoreCaseLikeAndPriceLessThan("%cell%phone%", 900.0f);
 * numDiscounted = products.discountOldInventory(0.15f, Year.now().getValue() - 1);
 * </pre>
 *
 * <p>Jakarta Persistence and Jakarta NoSQL define entity models that you
 * may use in Jakarta Data. You may use {@code jakarta.persistence.Entity}
 * and the corresponding entity-related annotations of the Jakarta Persistence
 * specification to define entities for relational databases.
 * You may use {@code jakarta.nosql.mapping.Entity} and the corresponding
 * entity-related annotations of the Jakarta NoSQL specification to define
 * entities for NoSQL databases. For other types of data stores, you may use
 * other entity models that are determined by the Jakarta Data provider
 * for the respective data store type.</p>
 *
 * <p>Methods of repository interfaces must be styled according to a
 * defined set of conventions, which instruct the container/runtime
 * about the desired data access operation to perform.
 * These conventions consist of
 * patterns of reserved keywords within the method name, as well as
 * annotations that are placed upon the method and its parameters.</p>
 *
 * <p>Built-in repository interfaces, such as {@link DataRepository}, are
 * parameterized with the entity type and id type. Other built-in repository
 * interfaces, such as {@link CrudRepository}, can be used in place of
 * {@link DataRepository}
 * and provide a base set of predefined repository methods
 * which serve as an optional starting point.
 * You can extend these built-in interfaces to add your own custom methods.
 * You can also copy individual method signatures from the
 * built-in repository methods onto your own, which is possible
 * because the built-in repository methods are consistent with the
 * same set of conventions that you use to write custom repository methods.</p>
 *
 * <p>Entity property names are computed from the fields and accessor methods
 * of the entity class and must be unique ignoring case. For simple entity
 * properties, the field or accessor method name is used as the entity property
 * name. In the case of embedded classes within entities, entity property names
 * are computed by concatenating the field or accessor method names at each level,
 * delimited by <code>_</code> or undelimited for query by method name (such as
 * <code>findByAddress_ZipCode</code> or <code>findByAddressZipCode</code>)
 * when referred to within repository method names, and delimited by
 * <code>.</code> when used within annotation values, such as for
 * {@link OrderBy#value} and {@link Query#value},</p>
 *
 * <pre>
 * &#64;Entity
 * public class Order {
 *     &#64;Id
 *     public String orderId;
 *     &#64;Embedded
 *     public Address address;
 *     ...
 * }
 *
 * &#64;Embeddable
 * public class Address {
 *     public int zipCode;
 *     ...
 * }
 *
 * &#64;Repository
 * public interface Orders {
 *     &#64;OrderBy("address.zipCode")
 *     List&lt;Order&gt; findByAddressZipCodeIn(List&lt;Integer&gt; zipCodes);
 *
 *     &#64;Query("SELECT o FROM Order o WHERE o.address.zipCode=?1")
 *     List&lt;Order&gt; forZipCode(int zipCode);
 *
 *     void save(Order order);
 * }
 * </pre>
 *
 * <p>In queries by method name, <code>Id</code> is an alias for the entity property
 * that is designated as the id. Entity property names that are used in queries
 * by method name must not contain reserved words.</p>
 *
 * <h2>Reserved Keywords for Query by Method Name</h2>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved Method Name Prefixes</b></caption>
 * <tr>
 * <td style="vertical-align: top"><b>Prefix</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>countBy</code></td>
 * <td>counts the number of entities</td>
 * <td><code>countByAgeGreaterThanEqual(ageLimit)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>deleteBy</code></td>
 * <td>for delete operations</td>
 * <td><code>deleteByStatus("DISCONTINUED")</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>existsBy</code></td>
 * <td>for determining existence</td>
 * <td><code>existsByYearHiredAndWageLessThan(2022, 60000)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By</code></td>
 * <td>for find operations</td>
 * <td><code>findByHeightBetween(minHeight, maxHeight)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>save</code></td>
 * <td>for save operations</td>
 * <td><code>save(Product newProduct)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>updateBy</code></td>
 * <td>for simple update operations</td>
 * <td><code>updateByIdSetModifiedOnAddPrice(productId, now, 10.0)</code></td></tr>
 * </table>
 *
 * <br><br>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved Keywords</b></caption>
 * <tr>
 * <td style="vertical-align: top"><b>Keyword</b></td>
 * <td style="vertical-align: top"><b>Applies to</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>And</code></td>
 * <td>conditions</td>
 * <td>Requires both conditions to be satisfied in order to match an entity.</td>
 * <td><code>findByNameLikeAndPriceLessThanEqual(namePattern, maxPrice)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Asc</code></td>
 * <td>sorting</td>
 * <td>Specifies ascending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAgeOrderByFirstNameAsc(age)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Between</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be within the range specified by two parameters.
 * The minimum is listed first, then the maximum.</td>
 * <td><code>findByAgeBetween(minAge, maxAge)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Contains</code></td>
 * <td>collections, strings</td>
 * <td>For Collection attributes, requires that the entity's attribute value,
 * which is a collection, includes the parameter value.
 * For String attributes, requires that any substring of the entity's attribute value
 * match the entity's attribute value, which can be a pattern with wildcard characters.</td>
 * <td><code>findByRecipientsContains(email)</code>
 * <br><code>findByDescriptionNotContains("refurbished")</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Desc</code></td>
 * <td>sorting</td>
 * <td>Specifies descending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAuthorLastNameOrderByYearPublishedDesc(surname)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Empty</code></td>
 * <td>collections</td>
 * <td>Requires that the entity's attribute is an empty collection or has a null value.</td>
 * <td><code>countByPhoneNumbersEmpty()</code>
 * <br><code>findByInviteesNotEmpty()</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>EndsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the end of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameEndsWith(surname)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>False</code></td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of false.</td>
 * <td><code>findByCanceledFalse()</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>First</code></td>
 * <td>find...By</td>
 * <td>Limits the amount of results that can be returned by the query
 * to the number that is specified after <code>First</code>,
 * or absent that to a single result.</td>
 * <td><code>findFirst25ByYearHiredOrderBySalaryDesc(int yearHired)</code>
 * <br><code>findFirstByYearHiredOrderBySalaryDesc(int yearHired)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>GreaterThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be larger than the parameter value.</td>
 * <td><code>findByStartTimeGreaterThan(startedAfter)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>GreaterThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as big as the parameter value.</td>
 * <td><code>findByAgeGreaterThanEqual(minimumAge)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>IgnoreCase</code></td>
 * <td>strings</td>
 * <td>Requires case insensitive comparison. For query conditions
 * as well as ordering, the <code>IgnoreCase</code> keyword can be
 * specified immediately following the entity property name.</td>
 * <td><code>countByStatusIgnoreCaseNotLike("%Delivered%")</code>
 * <br><code>findByZipcodeOrderByStreetIgnoreCaseAscHouseNumAsc(55904)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>In</code></td>
 * <td>all attribute types</td>
 * <td>Requires that the entity's attribute value be within the list that is the parameter value.</td>
 * <td><code>findByNameIn(names)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>LessThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be less than the parameter value.</td>
 * <td><code>findByStartTimeLessThan(startedBefore)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>LessThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as small as the parameter value.</td>
 * <td><code>findByAgeLessThanEqual(maximumAge)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Like</code></td>
 * <td>strings</td>
 * <td>Requires that the entity's attribute value match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameLike(namePattern)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Not</code></td>
 * <td>condition</td>
 * <td>Negates a condition.</td>
 * <td><code>deleteByNameNotLike(namePattern)</code>
 * <br><code>findByStatusNot("RUNNING")</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Null</code></td>
 * <td>nullable types</td>
 * <td>Requires that the entity's attribute has a null value.</td>
 * <td><code>findByEndTimeNull()</code>
 * <br><code>findByAgeNotNull()</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Or</code></td>
 * <td>conditions</td>
 * <td>Requires at least one of the two conditions to be satisfied in order to match an entity.</td>
 * <td><code>findByPriceLessThanEqualOrDiscountGreaterThanEqual(maxPrice, minDiscount)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>OrderBy</code></td>
 * <td>sorting</td>
 * <td>Sorts results of a <code>findBy</code> query according to one or more entity attributes.
 * Multiple attributes are delimited by <code>Asc</code> and <code>Desc</code>,
 * which indicate ascending and descending sort direction.
 * Precedence in sorting is determined by the order in which attributes are listed.</td>
 * <td><code>findByStatusOrderByYearHiredDescLastNameAsc(empStatus)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>StartsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the beginning of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameStartsWith(firstTwoLetters)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>True</code></td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of true.</td>
 * <td><code>findByAvailableTrue()</code></td></tr>
 *
 * </table>
 *
 * <h3>Reserved for Future Use</h3>
 * <p>
 * The specification does not define behavior for the following keywords, but reserves
 * them as keywords that must not be used as entity attribute names when using
 * Query by Method Name. This gives the specification the flexibility to add them in
 * future releases without introducing breaking changes to applications.
 * </p>
 * <p>
 * Reserved for query conditions: {@code AbsoluteValue}, {@code CharCount}, {@code ElementCount},
 * {@code Rounded}, {@code RoundedDown}, {@code RoundedUp}, {@code Trimmed},
 * {@code WithDay}, {@code WithHour}, {@code WithMinute}, {@code WithMonth},
 * {@code WithQuarter}, {@code WithSecond}, {@code WithWeek}, {@code WithYear}.
 * </p>
 * <p>
 * Reserved for find...By and count...By: {@code Distinct}.
 * </p>
 * <p>
 * Reserved for updates: {@code Add}, {@code Divide}, {@code Multiply}, {@code Set}, {@code Subtract}.
 * </p>
 *
 * <h3>Wildcard Characters</h3>
 * <p>
 * Wildcard characters for patterns are determined by the data access provider.
 * For Jakarta Persistence providers, <code>_</code> matches any one character
 * and <code>%</code> matches 0 or more characters.
 * </p>
 *
 * <h3>Logical Operator Precedence</h3>
 * <p>
 * For relational databases, the logical operator <code>And</code>
 * is evaluated on conditions before <code>Or</code> when both are specified
 * on the same method. Precedence for other database types is limited to
 * the capabilities of the database.
 * </p>
 *
 * <table style="width: 100%">
 * <caption><b>Return Types for Repository Methods</b></caption>
 * <tr>
 * <td style="vertical-align: top"><b>Method</b></td>
 * <td style="vertical-align: top"><b>Return Types</b></td>
 * <td style="vertical-align: top"><b>Notes</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>countBy...</code></td>
 * <td><code>long</code>, <code>Long</code>,
 * <br><code>int</code>, <code>Integer</code>,
 * <br><code>short</code>, <code>Short</code>,
 * <br><code>Number</code></td>
 * <td>Jakarta Persistence providers limit the maximum to <code>Integer.MAX_VALUE</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>deleteBy...</code>,
 * <br><code>updateBy...</code></td>
 * <td><code>void</code>, <code>Void</code>,
 * <br><code>boolean</code>, <code>Boolean</code>,
 * <br><code>long</code>, <code>Long</code>,
 * <br><code>int</code>, <code>Integer</code>,
 * <br><code>short</code>, <code>Short</code>,
 * <br><code>Number</code></td>
 * <td>Jakarta Persistence providers limit the maximum to <code>Integer.MAX_VALUE</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>existsBy...</code></td>
 * <td><code>boolean</code>, <code>Boolean</code></td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>E</code>,
 * <br><code>Optional&lt;E&gt;</code></td>
 * <td>For queries returning a single item (or none)</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>E[]</code>,
 * <br><code>Iterable&lt;E&gt;</code>,
 * <br><code>Streamable&lt;E&gt;</code>,
 * <br><code>Collection&lt;E&gt;</code></td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>Stream&lt;E&gt;</code></td>
 * <td>The caller must arrange to {@link java.util.stream.BaseStream#close() close}
 * all streams that it obtains from repository methods.</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>Collection</code> subtypes</td>
 * <td>The subtype must have a public default constructor and support <code>addAll</code> or <code>add</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>Page&lt;E&gt;</code>, <code>Iterator&lt;E&gt;</code></td>
 * <td>For use with pagination</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...By...</code></td>
 * <td><code>LinkedHashMap&lt;K, E&gt;</code></td>
 * <td>Ordered map of Id attribute value to entity</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>save(E)</code></td>
 * <td><code>E</code>,
 * <br><code>void</code>, <code>Void</code></td>
 * <td>For saving a single entity.</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>save(E...)</code>,
 * <br><code>save(Iterable&lt;E&gt;)</code>,
 * <br><code>save(Stream&lt;E&gt;)</code></td>
 * <td><code>void</code>, <code>Void</code>,
 * <br><code>E[]</code>,
 * <br><code>Iterable&lt;E&gt;</code>,
 * <br><code>Stream&lt;E&gt;</code>,
 * <br><code>Collection&lt;E&gt;</code>
 * <br><code>Collection</code> subtypes</td>
 * <td>For saving multiple entities.
 * <br>Collection subtypes must have a public default constructor
 * and support <code>addAll</code> or <code>add</code></td></tr>
 * </table>
 *
 * <h2>Parameters to Repository Methods</h2>
 *
 * <p>The parameters to a repository method correspond to the conditions that are
 * defined within the name of repository method (see reserved keywords above),
 * in the same order specified.
 * Most conditions, such as <code>Like</code> or <code>LessThan</code>,
 * correspond to a single method parameter. The exception to this rule is
 * <code>Between</code>, which corresponds to two method parameters.</p>
 *
 * <p>After all conditions are matched up with the corresponding parameters,
 * the remaining repository method parameters are used to enable other
 * capabilities such as pagination, limits, and sorting.</p>
 *
 * <h3>Limits</h3>
 *
 * <p>You can cap the number of results that can be returned by a single
 * invocation of a repository find method by adding a {@link Limit} parameter.
 * You can also limit the results to a positional range. For example,</p>
 *
 * <pre>
 * &#64;Query("SELECT o FROM Products o WHERE (o.fullPrice - o.salePrice) / o.fullPrice &gt;= ?1 ORDER BY o.salePrice DESC")
 * Product[] highlyDiscounted(float minPercentOff, Limit limit);
 *
 * ...
 * first50 = products.highlyDiscounted(0.30, Limit.of(50));
 * ...
 * second50 = products.highlyDiscounted(0.30, Limit.range(51, 100));
 * </pre>
 *
 * <h3>Pagination</h3>
 *
 * <p>You can request that results be paginated by adding a {@link Pageable}
 * parameter to a repository find method. For example,</p>
 *
 * <pre>
 * Product[] findByNameLikeOrderByAmountSoldDescNameAsc(
 *           String pattern, Pageable pagination);
 * ...
 * page1 = products.findByNameLikeOrderByAmountSoldDescNameAsc(
 *                  "%phone%", Pageable.ofSize(20));
 * </pre>
 *
 * <h3>Sorting at Runtime</h3>
 *
 * <p>When using pagination, you can dynamically supply sorting criteria
 * via the {@link Pageable#sortBy(Sort...)} and {@link Pageable#sortBy(Iterable)}
 * methods. For example,</p>
 *
 * <pre>
 * Product[] findByNameLike(String pattern, Pageable pagination);
 *
 * ...
 * Pageable pagination = Pageable.ofSize(25).sortBy(
 *                           Sort.desc("price"),
 *                           Sort.asc("name"));
 * page1 = products.findByNameLikeAndPriceBetween(
 *                 namePattern, minPrice, maxPrice, pagination);
 * </pre>
 *
 * <p>To supply sorting criteria dynamically without using pagination,
 * add one or more {@link Sort} parameters (or <code>Sort...</code>)
 * to a repository find method. For example,</p>
 *
 * <pre>
 * Product[] findByNameLike(String pattern, Limit max, Sort... sortBy);
 *
 * ...
 * page1 = products.findByNameLike(namePattern, Limit.of(25),
 *                                 Sort.desc("price"),
 *                                 Sort.desc("amountSold"),
 *                                 Sort.asc("name"));
 * </pre>
 *
 * <h2>Jakarta Validation</h2>
 *
 * <p>When a Jakarta Validation provider is present, validation constraints that are defined for entities
 * are automatically applied by the {@code save} and {@code saveAll} repository operations prior to saving
 * entities to the database. These methods raise {@code jakarta.validation.ConstraintViolationException}
 * if an entity is in violation of one or more validation constraints.</p>
 *
 * <h2>Jakarta Transactions</h2>
 *
 * <p>Repository methods can participate in global transactions.
 * If a global transaction is active on the thread where a repository method runs
 * and the data source that backs the repository is capable of transaction enlistment,
 * then the repository operation runs as part of the transaction.
 * The repository operation does not commit or roll back a transaction
 * that was already present on the thread, but it might mark the transaction
 * for rollback only ({@code jakarta.transaction.Status.STATUS_MARKED_ROLLBACK})
 * if the repository operation fails.</p>
 *
 * <p>When running in an environment where Jakarta Transactions and Jakarta CDI are
 * available, you can annotate repository methods with {@code jakarta.transaction.Transactional}
 * to define how the container manages transactions with respect to the repository
 * method.</p>
 *
 * <h2>Interceptor Annotations on Repository Methods</h2>
 *
 * <p>Interceptor bindings such as {@code jakarta.transaction.Transactional} can annotate a
 * repository method. The repository bean honors these annotations when running in an
 * environment where the Jakarta EE technology that provides the interceptor is available.</p>
 */
// TODO When can "By" be omitted and "All" added? Need to document that.
// TODO Does Jakarta NoSQL have the same or different wildcard characters? Document this
//       under: "Wildcard characters for patterns are determined by the data access provider"
// TODO Ensure we have all required supported return types listed.
module jakarta.data.api {
    exports jakarta.data.repository;
    exports jakarta.data.exceptions;
    opens jakarta.data.repository;
}