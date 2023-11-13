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

import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.page.Pageable;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.By;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;

import java.util.Collection;
import java.util.List;

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
 * public interface Products extends BasicRepository&lt;Product, Long&gt; {
 *
 *     &#64;Insert
 *     void create(Product prod);
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
 * products.create(newProduct);
 *
 * found = products.findByNameIgnoreCaseLikeAndPriceLessThan("%cell%phone%", 900.0f);
 *
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
 * about the desired data access operation to perform. These conventions
 * consist of patterns of reserved keywords within the method name,
 * method parameters with special meaning, method return types,
 * and annotations that are placed upon the method and its parameters.</p>
 *
 * <p>Built-in repository super interfaces, such as {@link DataRepository},
 * are provided as a convenient way to inherit commonly used methods and are
 * parameterized with the entity type and id type. Other built-in repository
 * interfaces, such as {@link BasicRepository}, can be used in place of
 * {@link DataRepository}
 * and provide a base set of predefined repository methods
 * which serve as an optional starting point.
 * You can extend these built-in interfaces to add your own custom methods.
 * You can also define your own repository interface without inheriting from the
 * built-in super interfaces. You can copy individual method signatures from the
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
 *     &#64;Save
 *     Order checkout(Order order);
 * }
 * </pre>
 *
 * <p>When using the <b>Query by Method Name</b> pattern
 * as well as the <b>Parameter-based Conditions</b> pattern,
 * <code>Id</code> is an alias for the entity property
 * that is designated as the id. Entity property names that are used in queries
 * by method name must not contain reserved words.</p>
 *
 * <h2>Methods with Entity Parameters</h2>
 *
 * <p>You can annotate a method with {@link Insert}, {@link Update}, {@link Save},
 * or {@link Delete} if the method accepts a single parameter, which must be one of:</p>
 *
 * <ul>
 * <li>An entity.</li>
 * <li>An array of entity (variable arguments array is permitted).</li>
 * <li>An {@link Iterable} of entity (subclasses such as {@link List} are permitted).</li>
 * </ul>
 *
 * <table style="width: 100%">
 * <caption><b>Lifecycle Annotations</b></caption>
 * <tr>
 * <td style="vertical-align: top; width: 10%"><b>Annotation</b></td>
 * <td style="vertical-align: top; width: 25%"><b>Description</b></td>
 * <td style="vertical-align: top; width: 65%"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td>{@link Delete}</td>
 * <td>deletes entities</td>
 * <td>{@code @Delete}<br><code>public void remove(person);</code></td></tr>
 *
 * <tr style="vertical-align: top"><td>{@link Insert}</td>
 * <td>creates new entities</td>
 * <td>{@code @Insert}<br><code>public List&lt;Employee&gt; add(List&lt;Employee&gt; newEmployees);</code></td></tr>
 *
 * <tr style="vertical-align: top"><td>{@link Save}</td>
 * <td>update if exists, otherwise insert</td>
 * <td>{@code @Save}<br><code>Product[] saveAll(Product... products)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td>{@link Update}</td>
 * <td>updates an existing entity</td>
 * <td>{@code @Update}<br><code>public boolean modify(Product modifiedProduct);</code></td></tr>
 * </table>
 *
 * <p>Refer to the JavaDoc of each annotation for more information.</p>
 *
 * <h2>Query by Method Name</h2>
 *
 * <p>Repository methods following the <b>Query by Method Name</b> pattern
 * must include the {@code By} keyword in the method name and must not include
 * any life cycle annotations on the method or any data access related annotations
 * on the method parameters. Query conditions
 * are determined by the portion of the method name following the {@code By} keyword.</p>
 *
 * <table style="width: 100%">
 * <caption><b>Query By Method Name</b></caption>
 * <tr>
 * <td style="vertical-align: top; width: 10%"><b>Prefix</b></td>
 * <td style="vertical-align: top; width: 25%"><b>Description</b></td>
 * <td style="vertical-align: top; width: 65%"><b>Example</b></td>
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
 * <tr style="vertical-align: top"><td><code>updateBy</code></td>
 * <td>for simple update operations</td>
 * <td><code>updateByIdSetModifiedOnAddPrice(productId, now, 10.0)</code></td></tr>
 * </table>
 *
 * <p>When using the <i>Query By Method Name</i> pattern
 * the conditions are defined by the portion of the repository method name
 * (referred to as the Predicate) that follows the {@code By} keyword,
 * in the same order specified.
 * Most conditions, such as <code>Like</code> or <code>LessThan</code>,
 * correspond to a single method parameter. The exception to this rule is
 * <code>Between</code>, which corresponds to two method parameters.</p>
 *
 * <p>Key-value and Wide-Column databases raise {@link UnsupportedOperationException}
 * for queries on attributes other than the identifier/key.</p>
 *
 * <h2>Reserved Keywords for Query by Method Name</h2>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Predicate</b></caption>
 * <tr>
 * <td style="vertical-align: top"><b>Keyword</b></td>
 * <td style="vertical-align: top"><b>Applies to</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * <td style="vertical-align: top"><b>Unavailable In</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>And</code></td>
 * <td>conditions</td>
 * <td>Requires both conditions to be satisfied in order to match an entity.</td>
 * <td><code>findByNameLikeAndPriceLessThanEqual(namePattern, maxPrice)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Between</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be within the range specified by two parameters.
 * The minimum is listed first, then the maximum.</td>
 * <td><code>findByAgeBetween(minAge, maxAge)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Contains</code></td>
 * <td>collections, strings</td>
 * <td>For Collection attributes, requires that the entity's attribute value,
 * which is a collection, includes the parameter value.
 * For String attributes, requires that any substring of the entity's attribute value
 * match the entity's attribute value, which can be a pattern with wildcard characters.</td>
 * <td><code>findByRecipientsContains(email)</code>
 * <br><code>findByDescriptionNotContains("refurbished")</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Empty</code></td>
 * <td>collections</td>
 * <td>Requires that the entity's attribute is an empty collection or has a null value.</td>
 * <td><code>countByPhoneNumbersEmpty()</code>
 * <br><code>findByInviteesNotEmpty()</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>EndsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the end of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameEndsWith(surname)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>False</code></td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of false.</td>
 * <td><code>findByCanceledFalse()</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>GreaterThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be larger than the parameter value.</td>
 * <td><code>findByStartTimeGreaterThan(startedAfter)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>GreaterThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as big as the parameter value.</td>
 * <td><code>findByAgeGreaterThanEqual(minimumAge)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>IgnoreCase</code></td>
 * <td>strings</td>
 * <td>Requires case insensitive comparison. For query conditions
 * as well as ordering, the <code>IgnoreCase</code> keyword can be
 * specified immediately following the entity property name.</td>
 * <td><code>countByStatusIgnoreCaseNotLike("%Delivered%")</code>
 * <br><code>findByZipcodeOrderByStreetIgnoreCaseAscHouseNumAsc(55904)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>In</code></td>
 * <td>all attribute types</td>
 * <td>Requires that the entity's attribute value be within the list that is the parameter value.</td>
 * <td><code>findByNameIn(names)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>LessThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be less than the parameter value.</td>
 * <td><code>findByStartTimeLessThan(startedBefore)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>LessThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as small as the parameter value.</td>
 * <td><code>findByAgeLessThanEqual(maximumAge)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Like</code></td>
 * <td>strings</td>
 * <td>Requires that the entity's attribute value match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameLike(namePattern)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Not</code></td>
 * <td>condition</td>
 * <td>Negates a condition.</td>
 * <td><code>deleteByNameNotLike(namePattern)</code>
 * <br><code>findByStatusNot("RUNNING")</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Null</code></td>
 * <td>nullable types</td>
 * <td>Requires that the entity's attribute has a null value.</td>
 * <td><code>findByEndTimeNull()</code>
 * <br><code>findByAgeNotNull()</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Or</code></td>
 * <td>conditions</td>
 * <td>Requires at least one of the two conditions to be satisfied in order to match an entity.</td>
 * <td><code>findByPriceLessThanEqualOrDiscountGreaterThanEqual(maxPrice, minDiscount)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>StartsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the beginning of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameStartsWith(firstTwoLetters)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>True</code></td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of true.</td>
 * <td><code>findByAvailableTrue()</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * </table>
 *
 * <br><br>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Subject</b></caption>
 * <tr>
 * <td style="vertical-align: top; width: 12%"><b>Keyword</b></td>
 * <td style="vertical-align: top; width: *%"><b>Applies to</b></td>
 * <td style="vertical-align: top; width: *"><b>Description</b></td>
 * <td style="vertical-align: top; width: 48%"><b>Example</b></td>
 * <td style="vertical-align: top; width: *"><b>Unavailable In</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>First</code></td>
 * <td>find...By</td>
 * <td>Limits the amount of results that can be returned by the query
 * to the number that is specified after <code>First</code>,
 * or absent that to a single result.</td>
 * <td><code>findFirst25ByYearHiredOrderBySalaryDesc(int yearHired)</code>
 * <br><code>findFirstByYearHiredOrderBySalaryDesc(int yearHired)</code></td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 * </table>
 *
 * <br><br>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Order Clause</b></caption>
 * <tr>
 * <td style="vertical-align: top"><b>Keyword</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td><code>Asc</code></td>
 * <td>Specifies ascending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAgeOrderByFirstNameAsc(age)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>Desc</code></td>
 * <td>Specifies descending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAuthorLastNameOrderByYearPublishedDesc(surname)</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>OrderBy</code></td>
 * <td>Sorts results of a <code>findBy</code> query according to one or more entity attributes.
 * Multiple attributes are delimited by <code>Asc</code> and <code>Desc</code>,
 * which indicate ascending and descending sort direction.
 * Precedence in sorting is determined by the order in which attributes are listed.</td>
 * <td><code>findByStatusOrderByYearHiredDescLastNameAsc(empStatus)</code></td></tr>
 *
 * </table>
 *
 * <p>Key-value and Wide-Column databases raise {@link UnsupportedOperationException}
 * if an order clause is present.</p>
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
 * <h2>Return Types for Repository Methods</h2>
 *
 * <table style="width: 100%">
 * <caption><b>Return Types when using Query by Method Name and Parameter-based Conditions</b></caption>
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
 * <td>For determining existence.</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...</code>,
 * <br><code>find...By...</code></td>
 * <td><code>E</code>,
 * <br><code>Optional&lt;E&gt;</code></td>
 * <td>For queries returning a single item (or none)</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...</code>,
 * <br><code>find...By...</code></td>
 * <td><code>E[]</code>,
 * <br><code>Iterable&lt;E&gt;</code>,
 * <br><code>Streamable&lt;E&gt;</code>,
 * <br><code>Collection&lt;E&gt;</code></td>
 * <td>For queries where it is possible to return more than 1 item.</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...</code>,
 * <br><code>find...By...</code></td>
 * <td><code>Stream&lt;E&gt;</code></td>
 * <td>The caller must arrange to {@link java.util.stream.BaseStream#close() close}
 * all streams that it obtains from repository methods.</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...</code>,
 * <br><code>find...By...</code></td>
 * <td><code>Collection</code> subtypes</td>
 * <td>The subtype must have a public default constructor and support <code>addAll</code> or <code>add</code></td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...(..., Pageable)</code>,
 * <br><code>find...By...(..., Pageable)</code></td>
 * <td><code>Page&lt;E&gt;</code>, <code>KeysetAwarePage&lt;E&gt;</code>,
 * <br><code>Slice&lt;E&gt;</code>, <code>KeysetAwareSlice&lt;E&gt;</code></td>
 * <td>For use with pagination</td></tr>
 *
 * <tr style="vertical-align: top"><td><code>find...</code>,
 * <br><code>find...By...</code></td>
 * <td><code>LinkedHashMap&lt;K, E&gt;</code></td>
 * <td>Ordered map of Id attribute value to entity</td></tr>
 *
 * </table>
 *
 * <h3>Return Types for Annotated Methods</h3>
 *
 * <p>Return types that are valid when using {@link Query} depend on the Query Language
 * operation that is performed. For queries that correspond to operations in the above
 * table, the same return types must be supported as for the Query-by-Method-Name and
 * Parameter-based Conditions patterns.</p>
 *
 * <p>Refer to the {@link Insert}, {@link Update}, {@link Save}, and {@link Delete}
 * JavaDoc for valid return types when using those annotations. Whenever the
 * return type is an {@link Iterable} subtype that is a concrete class,
 * the class must have a public default constructor and support
 * {@link Collection#addAll addAll} or {@link Collection#add add}.</p>
 *
 * <h2>Parameter-based Conditions</h2>
 *
 * <p>When using the <i>Parameter-based Conditions</i> pattern,
 * the method name must not include the {@code By} keyword or at least one
 * of the parameters must be annotated with a data access related annotation.
 * The query conditions are defined by the method parameters.
 * You can annotate method parameters with the {@link By} annotation
 * to specify the name of the entity attribute that the parameter value
 * is to be compared with. Otherwise, the method parameter name
 * must match the name of an entity attribute and you must compile
 * with the {@code -parameters} compiler option that makes parameter
 * names available at run time.
 * The {@code _} character can be used in method parameter names to
 * reference embedded attributes. All conditions are considered to be
 * the equality condition. All conditions must match in order to
 * retrieve an entity.</p>
 *
 * <p>The following examples illustrate the difference between
 * <i>Query By Method Name</i> and <i>Parameter-based Conditions</i> patterns.
 * Both methods accept the same parameters and have the same behavior.</p>
 *
 * <pre>
 * // Query by Method Name:
 * Vehicle[] findByMakeAndModelAndYear(String makerName, String model, int year, Sort... sorts);
 *
 * // Parameter-based Conditions:
 * Vehicle[] find(String make, String model, int year, Sort... sorts);
 * </pre>
 *
 * <h2>Additional Method Parameters</h2>
 *
 * <p>When using {@code @Query} or the
 * <i>Query By Method Name</i> pattern or the
 * <i>Parameter-based Find</i> pattern,
 * after conditions are determined from the corresponding parameters,
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
 * <h2>Repository Default Methods</h2>
 *
 * <p>You can compose default methods on your repository interface to supply
 * user-defined implementation.</p>
 *
 * <h2>Resource Accessor Methods</h2>
 *
 * <p>For some advanced scenarios, you might need access to an
 * underlying resource from the Jakarta Data provider, such as a
 * {@code jakarta.persistence.EntityManager},
 * {@code javax.sql.DataSource}, or
 * {@code java.sql.Connection}</p>
 *
 * <p>To obtain the above, you can define accessor methods on your repository interface,
 * where the method has no parameters and its result value is one of the
 * aforementioned types. When you invoke the method, the Jakarta Data provider
 * supplies an instance of the requested type of resource.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface Cars extends BasicRepository&lt;Car, Long&gt; {
 *     ...
 *
 *     EntityManager getEntityManager();
 *
 *     default Car[] advancedSearch(SearchOptions filter) {
 *         EntityManager em = getEntityManager();
 *         ... use entity manager
 *         return results;
 *     }
 * }
 * </pre>
 *
 * <p>If the resource type inherits from {@link AutoCloseable} and you invoke the
 * accessor method from a repository default method, the Jakarta Data provider
 * automatically closes the resource after the default method ends.
 * If you invoke the accessor method from outside the scope of a default method,
 * you are responsible for closing the resource instance.</p>
 *
 * <h2>Precedence of Repository Methods</h2>
 *
 * <p>The following precedence, from highest to lowest, is used when interpreting the
 * meaning of repository methods.</p>
 *
 * <ol>
 * <li>If you define a method as a <b>Java default method</b> and provide implementation,
 * then your provided implementation is used.</li>
 * <li>If you define a method with a <b>Resource Accessor Method</b> return type,
 * then the method is implemented as a Resource Accessor Method.</li>
 * <li>If you annotate a method with {@link Query}, then the method is implemented
 * to run the corresponding Query Language query.</li>
 * <li>If you annotate a method with an annotation that defines the type of operation
 * ({@link Insert}, {@link Update}, {@link Save}, or {@link Delete}),
 * then the annotation determines how the method is implemented.
 * <li>If you annotate any of the method parameters with a data access related annotation,
 * then <b>Parameter-based Conditions</b> determine the implementation of the method.</li>
 * <li>If you define a method according to the <b>Query by Method Name pattern</b> naming conventions,
 * then the implementation follows the Query by Method Name pattern.</li>
 * <li>Otherwise, <b>Parameter-based Conditions</b> determine the implementation of the method.
 * You must compile with the {@code -parameters} compiler option that makes parameter names
 * available at run time.</li>
 * </ol>
 *
 * <h2>Identifying the type of Entity</h2>
 *
 * <p>Most repository methods perform operations related to a type of entity. In some cases,
 * the entity type is explicit within the signature of the repository method, and in other
 * cases, such as {@code countBy...} and {@code existsBy...} the entity type cannot be determined
 * from the method signature and a primary entity type must be defined for the repository.</p>
 *
 * <b>Methods where the entity type is explicitly specified:</b>
 *
 * <ul>
 * <li>For repository methods that are annotated with {@link Insert}, {@link Update},
 * {@link Save}, or {@link Delete} where the method parameter is a type, an array of type,
 * or is parameterized with a type that is annotated as an entity,
 * the entity type is determined from the method parameter type.</li>
 * <li>For find and delete methods where the return type is a type, an array of type,
 * or is parameterized with a type that is annotated as an entity,
 * such as {@code MyEntity}, {@code MyEntity[]}, or {@code Page<MyEntity>},
 * the entity type is determined from the method return type.</li>
 * </ul>
 *
 * <b>Identifying a Primary Entity Type:</b>
 *
 * <p>The following precedence, from highest to lowest, is used to determine a primary
 * entity type for a repository.</p>
 *
 * <ol>
 * <li>You can explicitly define the primary entity type for a repository interface
 * by having the repository interface inherit from a super interface such as
 * {@link CrudRepository} where the primary entity type is the type of the
 * super interface's first type parameter. For example, {@code Product}, in,
 * <pre>
 * &#64;Repository
 * public interface Products extends CrudRepository&lt;Product, Long&gt; {
 *     // applies to the primary entity type: Product
 *     int countByPriceLessThan(float max);
 * }
 * </pre>
 * </li>
 * <li>Otherwise, if you define life cycle methods ({@link Insert}, {@link Update},
 * {@link Save}, or {@link Delete}) where the method parameter is a type, an array of type,
 * or is parameterized with a type that is annotated as an entity,
 * and all of these methods share the same entity type,
 * then the primary entity type for the repository is that entity type. For example,
 * <pre>
 * &#64;Repository
 * public interface Products {
 *     &#64;Insert
 *     List&lt;Product&gt; add(List&lt;Product&gt; p);
 *
 *     &#64;Update
 *     Product modify(Product p);
 *
 *     &#64;Save
 *     Product[] save(Product... p);
 *
 *     // applies to the primary entity type: Product
 *     boolean existsByName(String name);
 * }
 * </pre>
 * </li>
 * </ol>
 *
 * <h2>Jakarta Validation</h2>
 *
 * <p>When a Jakarta Validation provider is present, constraints that are defined on
 * repository method parameters and return values are validated according to the section,
 * "Method and constructor validation", of the Jakarta Validation specification.</p>
 *
 * <p>The {@code jakarta.validation.Valid} annotation opts in to cascading validation,
 * causing constraints within the objects that are supplied as parameters
 * or returned as results to also be validated.</p>
 *
 * <p>Repository methods raise {@code jakarta.validation.ConstraintViolationException}
 * if validation fails.</p>
 *
 * <p>The following is an example of method validation, where the
 * parameter to {@code findByEmailIn} must not be the empty set,
 * and cascading validation, where the {@code Email} and {@code NotNull} constraints
 * on the entity that is supplied to {@code save} are validated,</p>
 *
 * <pre>
 * import jakarta.validation.Valid;
 * import jakarta.validation.constraints.Email;
 * import jakarta.validation.constraints.NotEmpty;
 * import jakarta.validation.constraints.NotNull;
 * ...
 *
 * &#64;Repository
 * public interface AddressBook extends DataRepository&lt;Contact, Long&gt; {
 *
 *     List&lt;Contact&gt; findByEmailIn(&#64;NotEmpty Set&lt;String&gt; emails);
 *
 *     &#64;Save
 *     void save(&#64;Valid Contact c);
 * }
 *
 * &#64;Entity
 * public class Contact {
 *     &#64;Email
 *     &#64;NotNull
 *     public String email;
 *     &#64;Id
 *     public long id;
 *     ...
 * }
 * </pre>
 *
 * <h2>Jakarta Persistence</h2>
 *
 * <h3>Persistence Context</h3>
 *
 * <p>When the Jakarta Data provider is backed by a Jakarta Persistence provider,
 * repository operations must behave as though backed by a stateless Entity Manager
 * in that persistence context is not preserved across the end of repository methods.
 * If you retrieve an entity via a repository and then modify the entity,
 * the modifications are not persisted to the database unless you explicitly invoke
 * a {@link Save} or {@link Update} operation in order to persist it.</p>
 *
 * <p>Here is an example with {@link BasicRepository#findById(K)} and
 * {@link BasicRepository#save(S)} operations:</p>
 *
 * <pre>
 * product = products.findById(prodNum).orElseThrow();
 * product.price = produce.price + 0.50;
 * product = products.save(product);
 * </pre>
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
// TODO Does Jakarta NoSQL have the same or different wildcard characters? Document this
//       under: "Wildcard characters for patterns are determined by the data access provider"
module jakarta.data {
    exports jakarta.data;
    exports jakarta.data.page;
    exports jakarta.data.repository;
    exports jakarta.data.exceptions;
    opens jakarta.data.repository;
}