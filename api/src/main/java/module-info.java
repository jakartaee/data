/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.By;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;

import java.util.Collection;
import java.util.List;

/**
 * <p>Jakarta Data standardizes a programming model where data is represented by
 * simple Java classes and where operations on data are represented by interface
 * methods.</p>
 *
 * <p>The application defines simple Java objects called entities to represent
 * data in the database. Fields or accessor methods designate each entity property.
 * For example,</p>
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
 * <p>A repository is an interface annotated with the {@link Repository} annotation.
 * A repository declares methods which perform queries and other operations on entities.
 * For example,</p>
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
 * <p>Repository interfaces are implemented by the container/runtime and are made
 * available to applications via the {@code jakarta.inject.Inject} annotation. For
 * example,</p>
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
 * <p>Jakarta Persistence and Jakarta NoSQL define programming models for entity
 * classes that may be used with Jakarta Data:</p>
 * <ul>
 * <li>{@code jakarta.persistence.Entity} and the corresponding entity-related
 *    annotations of the Jakarta Persistence specification may be used to
 *    define entities stored in a relational database, or</li>
 * <li>{@code jakarta.nosql.mapping.Entity} and the corresponding entity-related
 *     annotations of the Jakarta NoSQL specification may be used to define
 *     entities stored in a NoSQL database.
 * </ul>
 * <p>A Jakarta Data provider may define its own programming model for entity
 *    classes representing some other arbitrary kind of data.</p>
 *
 * <p>Methods of repository interfaces must be styled according to a
 * well-defined set of conventions, which instruct the container/runtime
 * about the desired data access operation to perform. These conventions
 * consist of patterns of reserved keywords within the method name, method
 * parameters with special meaning, method return types, and annotations
 * placed upon the method and its parameters.</p>
 *
 * <p>Built-in repository superinterfaces, such as {@link DataRepository},
 * are provided as a convenient way to inherit commonly used methods and
 * are parameterized by the entity type and by its id type. Other built-in
 * repository interfaces, such as {@link BasicRepository}, may be used in
 * place of {@link DataRepository} and provide a base set of predefined
 * repository operations serving as an optional starting point. The Java
 * application programmer may extend these built-in interfaces, adding
 * custom methods. Alternatively, the programmer may define a repository
 * interface without inheriting the built-in superinterfaces. A programmer
 * may even copy individual method signatures from the built-in repositories
 * to a repository which does not inherit any built-in superinterface. This
 * is possible because the methods of the built-in repository superinterfaces
 * respect the conventions defined for custom repository methods.</p>
 *
* <p>The following example shows an entity class, an embeddable class, and
 * a repository interface:</p>
 * <pre>
 * &#64;Entity
 * public class Purchase {
 *     &#64;Id
 *     public String purchaseId;
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
 * public interface Purchases {
 *     &#64;OrderBy("address.zipCode")
 *     List&lt;Purchase&gt; findByAddressZipCodeIn(List&lt;Integer&gt; zipCodes);
 *
 *     &#64;Query("SELECT o FROM Purchase o WHERE o.address.zipCode=?1")
 *     List&lt;Purchase&gt; forZipCode(int zipCode);
 *
 *     &#64;Save
 *     Purchase checkout(Purchase purchase);
 * }
 * </pre>
 *
 * <h2>Persistent Field Names</h2>
 *
 * <p>Each persistent field of an entity or embeddable class is assigned a
 * name:</p>
 * <ul>
 * <li>when direct field access is used, the name of a persistent field is
 *     simply the name of the Java field, but</li>
 * <li>when property-based access is used, the name of the field is derived
 *     from the accessor methods, according to JavaBeans conventions.</li>
 * </ul>
 * <p>Within a given entity class or embeddable class, names assigned to
 * persistent fields must be unique ignoring case.</p>
 * <p>Furthermore, within the context of a given entity, each persistent field
 * of an embeddable class reachable by navigation from the entity class may be
 * assigned a compound name. The compound name is obtained by concatenating the
 * names assigned to each field traversed by navigation from the entity class
 * to the persistent field of the embedded class, optionally joined by a
 * delimiter.</p>
 * <ul>
 * <li>For parameters of a {@link Find} method, the delimiter is {@code _}.
 * <li>For path expressions within a {@linkplain Query query}, the delimiter
 *     is {@code .}.
 * <li>For method names in <i>Query by Method Name</i>, the delimiter is
 *     {@code _} and it is optional. For example, {@code findByAddress_ZipCode}
 *     or {@code findByAddressZipCode} are both legal.
 * <li>For arguments to constructor methods of {@link Sort}, the delimiter
 *     is {@code _} or {@code .}.
 * <li>For the {@code value} member of the {@link OrderBy} or {@link By}
 *     annotation the delimiter is {@code _} or {@code .}.
 * </ul>
 *
 * <p>For <i>Query by Method Name</i>, {@code Id} is an alias for the unique
 * identifier field or property of an entity.<p>
 *
 * <p>A persistent field name used in a <i>Query by Method Name</i> must not
 * contain a keyword reserved by <i>Query by Method Name</i>.</p>
 *
 * <h2>Lifecycle methods</h2>
 *
 * <p>A lifecycle method makes changes to persistent data in the data store.
 * A lifecycle method must be annotated with a lifecycle annotation such as
 * {@link Insert}, {@link Update}, {@link Save}, or {@link Delete}. The
 * method must accept a single parameter, whose type is either:</p>
 *
 * <ul>
 * <li>the class of the entity, or</li>
 * <li>{@code Iterable<E>} or {@code E[]} where {@code E} is the class of the
 *     entities.</li>
 * </ul>
 *
 * <table style="width: 100%">
 * <caption><b>Lifecycle Annotations</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top; width: 10%"><b>Annotation</b></td>
 * <td style="vertical-align: top; width: 25%"><b>Description</b></td>
 * <td style="vertical-align: top; width: 65%"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@link Delete}</td>
 * <td>deletes entities</td>
 * <td>{@code @Delete}<br>{@code public void remove(person);}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@link Insert}</td>
 * <td>creates new entities</td>
 * <td>{@code @Insert}<br>{@code public List<Employee> add(List<Employee> newEmployees);}</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@link Save}</td>
 * <td>update if exists, otherwise insert</td>
 * <td>{@code @Save}<br>{@code Product[] saveAll(Product... products)}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@link Update}</td>
 * <td>updates an existing entity</td>
 * <td>{@code @Update}<br>{@code public boolean modify(Product modifiedProduct);}</td></tr>
 * </table>
 *
 * <p>Refer to the JavaDoc of each annotation for more information.</p>
 *
 * <h2>Query by Method Name</h2>
 *
 * <p>Repository methods following the <b>Query by Method Name</b> pattern
 * must include the {@code By} keyword in the method name and must not include
 * the {@code @Find} annotation, {@code @Query} annotation, or
 * any life cycle annotations on the method or any data access related annotations
 * on the method parameters. Query conditions
 * are determined by the portion of the method name following the {@code By} keyword.</p>
 *
 * <table id="methodNamePrefixes" style="width: 100%">
 * <caption><b>Query By Method Name</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top; width: 10%"><b>Prefix</b></td>
 * <td style="vertical-align: top; width: 25%"><b>Description</b></td>
 * <td style="vertical-align: top; width: 65%"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top"><td>{@code countBy}</td>
 * <td>counts the number of entities</td>
 * <td>{@code countByAgeGreaterThanEqual(ageLimit)}</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code deleteBy}</td>
 * <td>for delete operations</td>
 * <td>{@code deleteByStatus("DISCONTINUED")}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code existsBy}</td>
 * <td>for determining existence</td>
 * <td>{@code existsByYearHiredAndWageLessThan(2022, 60000)}</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code find...By}</td>
 * <td>for find operations</td>
 * <td>{@code findByHeightBetween(minHeight, maxHeight)}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code updateBy}</td>
 * <td>for simple update operations</td>
 * <td>{@code updateByIdSetModifiedOnAddPrice(productId, now, 10.0)}</td></tr>
 * </table>
 *
 * <p>When using the <i>Query By Method Name</i> pattern
 * the conditions are defined by the portion of the repository method name
 * (referred to as the Predicate) that follows the {@code By} keyword,
 * in the same order specified.
 * Most conditions, such as {@code Like} or {@code LessThan},
 * correspond to a single method parameter. The exception to this rule is
 * {@code Between}, which corresponds to two method parameters.</p>
 *
 * <p>Key-value and Wide-Column databases raise {@link UnsupportedOperationException}
 * for queries on attributes other than the identifier/key.</p>
 *
 * <h3>Reserved Keywords for Query by Method Name</h3>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Predicate</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top"><b>Keyword</b></td>
 * <td style="vertical-align: top"><b>Applies to</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * <td style="vertical-align: top"><b>Unavailable In</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code And}</td>
 * <td>conditions</td>
 * <td>Requires both conditions to be satisfied in order to match an entity.</td>
 * <td>{@code findByNameLikeAndPriceLessThanEqual(namePattern, maxPrice)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code Between}</td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be within the range specified by two parameters,
 * inclusive of the parameters. The minimum is listed first, then the maximum.</td>
 * <td>{@code findByAgeBetween(minAge, maxAge)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code Contains}</td>
 * <td>collections, strings</td>
 * <td>For Collection attributes, requires that the entity's attribute value,
 * which is a collection, includes the parameter value.
 * For String attributes, requires that any substring of the entity's attribute value
 * match the entity's attribute value, which can be a pattern with wildcard characters.</td>
 * <td>{@code findByRecipientsContains(email)}
 * <br>{@code findByDescriptionNotContains("refurbished")}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code Empty}</td>
 * <td>collections</td>
 * <td>Requires that the entity's attribute is an empty collection or has a null value.</td>
 * <td>{@code countByPhoneNumbersEmpty()}
 * <br>{@code findByInviteesNotEmpty()}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code EndsWith}</td>
 * <td>strings</td>
 * <td>Requires that the characters at the end of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td>{@code findByNameEndsWith(surname)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code False}</td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of false.</td>
 * <td>{@code findByCanceledFalse()}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code GreaterThan}</td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be larger than the parameter value.</td>
 * <td>{@code findByStartTimeGreaterThan(startedAfter)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code GreaterThanEqual}</td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as big as the parameter value.</td>
 * <td>{@code findByAgeGreaterThanEqual(minimumAge)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code IgnoreCase}</td>
 * <td>strings</td>
 * <td>Requires case insensitive comparison. For query conditions
 * as well as ordering, the {@code IgnoreCase} keyword can be
 * specified immediately following the entity property name.</td>
 * <td>{@code countByStatusIgnoreCaseNotLike("%Delivered%")}
 * <br>{@code findByZipcodeOrderByStreetIgnoreCaseAscHouseNumAsc(55904)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code In}</td>
 * <td>all attribute types</td>
 * <td>Requires that the entity's attribute value be within the list that is the parameter value.</td>
 * <td>{@code findByNameIn(names)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code LessThan}</td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be less than the parameter value.</td>
 * <td>{@code findByStartTimeLessThan(startedBefore)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code LessThanEqual}</td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as small as the parameter value.</td>
 * <td>{@code findByAgeLessThanEqual(maximumAge)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code Like}</td>
 * <td>strings</td>
 * <td>Requires that the entity's attribute value match the parameter value, which can be a pattern.</td>
 * <td>{@code findByNameLike(namePattern)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code Not}</td>
 * <td>condition</td>
 * <td>Negates a condition.</td>
 * <td>{@code deleteByNameNotLike(namePattern)}
 * <br>{@code findByStatusNot("RUNNING")}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code Null}</td>
 * <td>nullable types</td>
 * <td>Requires that the entity's attribute has a null value.</td>
 * <td>{@code findByEndTimeNull()}
 * <br>{@code findByAgeNotNull()}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code Or}</td>
 * <td>conditions</td>
 * <td>Requires at least one of the two conditions to be satisfied in order to match an entity.</td>
 * <td>{@code findByPriceLessThanEqualOrDiscountGreaterThanEqual(maxPrice, minDiscount)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code StartsWith}</td>
 * <td>strings</td>
 * <td>Requires that the characters at the beginning of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td>{@code findByNameStartsWith(firstTwoLetters)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code True}</td>
 * <td>boolean</td>
 * <td>Requires that the entity's attribute value has a boolean value of true.</td>
 * <td>{@code findByAvailableTrue()}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column</td></tr>
 *
 * </table>
 *
 * <br><br>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Subject</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top; width: 12%"><b>Keyword</b></td>
 * <td style="vertical-align: top; width: *%"><b>Applies to</b></td>
 * <td style="vertical-align: top; width: *"><b>Description</b></td>
 * <td style="vertical-align: top; width: 48%"><b>Example</b></td>
 * <td style="vertical-align: top; width: *"><b>Unavailable In</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code First}</td>
 * <td>find...By</td>
 * <td>Limits the amount of results that can be returned by the query
 * to the number that is specified after {@code First},
 * or absent that to a single result.</td>
 * <td>{@code findFirst25ByYearHiredOrderBySalaryDesc(int yearHired)}
 * <br>{@code findFirstByYearHiredOrderBySalaryDesc(int yearHired)}</td>
 * <td style="font-family:sans-serif; font-size:0.8em">Key-value<br>Wide-Column<br>Document<br>Graph</td></tr>
 * </table>
 *
 * <br><br>
 *
 * <table style="width: 100%">
 * <caption><b>Reserved for Order Clause</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top"><b>Keyword</b></td>
 * <td style="vertical-align: top"><b>Description</b></td>
 * <td style="vertical-align: top"><b>Example</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code Asc}</td>
 * <td>Specifies ascending sort order for {@code findBy} queries</td>
 * <td>{@code findByAgeOrderByFirstNameAsc(age)}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code Desc}</td>
 * <td>Specifies descending sort order for {@code findBy} queries</td>
 * <td>{@code findByAuthorLastNameOrderByYearPublishedDesc(surname)}</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code OrderBy}</td>
 * <td>Sorts results of a {@code findBy} query according to one or more entity attributes.
 * Multiple attributes are delimited by {@code Asc} and {@code Desc},
 * which indicate ascending and descending sort direction.
 * Precedence in sorting is determined by the order in which attributes are listed.</td>
 * <td>{@code findByStatusOrderByYearHiredDescLastNameAsc(empStatus)}</td></tr>
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
 * For Jakarta Persistence providers, {@code _} matches any one character
 * and {@code %} matches 0 or more characters.
 * </p>
 *
 * <h3>Logical Operator Precedence</h3>
 * <p>
 * For relational databases, the logical operator {@code And}
 * is evaluated on conditions before {@code Or} when both are specified
 * on the same method. Precedence for other database types is limited to
 * the capabilities of the database.
 * </p>
 *
 * <h2>Return Types for Repository Methods</h2>
 *
 * <p>The following is a table of valid return types.
 * The <b>Method</b> column shows name patterns for <i>Query by Method Name</i>.
 * For methods with the {@link Query} annotation or <i>Parameter-based Conditions</i>,
 * which have flexible naming, refer to the equivalent <i>Query by Method Name</i>
 * pattern in the table. For example, to identify the valid return types for a method,
 * {@code findNamed(String name, PageRequest pagination)}, refer to the row for
 * {@code find...By...(..., PageRequest)}.</p>
 *
 * <table style="width: 100%">
 * <caption><b>Return Types when using Query by Method Name and Parameter-based Conditions</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top"><b>Method</b></td>
 * <td style="vertical-align: top"><b>Return Types</b></td>
 * <td style="vertical-align: top"><b>Notes</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code countBy...}</td>
 * <td>{@code long},
 * <br>{@code int}</td>
 * <td>Jakarta Persistence providers limit the maximum to {@code Integer.MAX_VALUE}</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code deleteBy...},
 * <br>{@code updateBy...}</td>
 * <td>{@code void},
 * <br>{@code boolean},
 * <br>{@code long},
 * <br>{@code int}</td>
 * <td>Jakarta Persistence providers limit the maximum to {@code Integer.MAX_VALUE}</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code existsBy...}</td>
 * <td>{@code boolean}</td>
 * <td>For determining existence.</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code find...By...}</td>
 * <td>{@code E},
 * <br>{@code Optional<E>}</td>
 * <td>For queries returning a single item (or none)</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code find...By...}</td>
 * <td>{@code E[]},
 * <br>{@code List<E>}</td>
 * <td>For queries where it is possible to return more than 1 item.</td></tr>
 *
 * <tr style="vertical-align: top"><td>{@code find...By...}</td>
 * <td>{@code Stream<E>}</td>
 * <td>The caller must arrange to {@link java.util.stream.BaseStream#close() close}
 * all streams that it obtains from repository methods.</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>{@code find...By...(..., PageRequest)}</td>
 * <td>{@code Page<E>}, {@code CursoredPage<E>}</td>
 * <td>For use with pagination</td></tr>
 *
 * </table>
 *
 * <h3>Return Types for Annotated Methods</h3>
 *
 * <p>The legal return types for a method annotated {@link Query} depend on
 * the Query Language operation that is performed. For queries that correspond
 * to operations in the above table, the same return types must be supported as
 * for the Query-by-Method-Name and Parameter-based Conditions patterns.</p>
 *
 * <p>The legal return types for a method annotated {@link Insert}, {@link Update},
 * {@link Save}, {@link Delete}, or {@link Find} are specified by the API documentation
 * for those annotations.</p>
 *
 * <h2>Parameter-based Automatic Query Methods</h2>
 *
 * <p>The {@link Find} annotation indicates that the repository method is
 * a parameter-based automatic query method. In this case, the method name
 * does not determine the semantics of the method, and the query conditions
 * are defined by the method parameters.</p>
 *
 * <p>A parameter may be annotated with the {@link By} annotation to specify
 * the name of the entity attribute that the argument is to be compared with.
 * If the {@link By} annotation is missing, the method parameter name must
 * match the name of an entity attribute and the repository must be compiled
 * with the {@code -parameters} compiler option so that parameter names are
 * available at run time. The {@code _} character may be used in a method
 * parameter name to reference embedded attributes. All conditions are
 * considered to be the equality condition. All conditions must match in
 * order to retrieve an entity.</p>
 *
 * <p>The following examples illustrate the difference between Query By Method
 * Name and parameter-based automatic query methods. Both methods accept the
 * same parameters and have the same behavior.</p>
 *
 * <pre>
 * // Query by Method Name:
 * Vehicle[] findByMakeAndModelAndYear(String makerName, String model, int year, {@code Sort<?>...} sorts);
 *
 * // Parameter-based Conditions:
 * {@code @Find}
 * Vehicle[] searchFor(String make, String model, int year, {@code Sort<?>...} sorts);
 * </pre>
 *
 * <h2>Basic Types</h2>
 *
 * <p>The following is a list of valid basic entity attribute types.
 * These can be used as the types of repository method parameters
 * for the respective entity attribute.</p>
 *
 * <table style="width: 100%">
 * <caption><b>Basic Types for Entity Attributes</b></caption>
 * <tr style="background-color:#ccc">
 * <td style="vertical-align: top; width:20%"><b>Category</b></td>
 * <td style="vertical-align: top; width:20%"><b>Basic Types</b></td>
 * <td style="vertical-align: top; width:*"><b>Notes</b></td>
 * </tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>Primitives and primitive wrappers</td>
 * <td>{@code boolean} and {@link Boolean}
 * <br>{@code byte} and {@link Byte}
 * <br>{@code char} and {@link Character}
 * <br>{@code double} and {@link Double}
 * <br>{@code float} and {@link Float}
 * <br>{@code int} and {@link Integer}
 * <br>{@code long} and {@link Long}
 * <br>{@code short} and {@link Short}</td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top"><td>Binary data</td>
 * <td>{@code byte[]}</td>
 * <td>Not sortable.</td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>Enumerated types</td>
 * <td>{@code enum} types</td>
 * <td>It is provider-specific whether sorting is based on
 * {@link Enum#ordinal()} or {@link Enum#name()}.
 * The Jakarta Persistence default of {@code ordinal} can be
 * overridden with the {@code jakarta.persistence.Enumerated}
 * annotation.</td></tr>
 *
 * <tr style="vertical-align: top"><td>Large numbers</td>
 * <td>{@link java.math.BigDecimal}
 * <br>{@link java.math.BigInteger}</td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>Textual data</td>
 * <td>{@link String}</td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top"><td>Time and Dates</td>
 * <td>{@link java.time.Instant}
 * <br>{@link java.time.LocalDate}
 * <br>{@link java.time.LocalDateTime}
 * <br>{@link java.time.LocalTime}</td>
 * <td></td></tr>
 *
 * <tr style="vertical-align: top; background-color:#eee"><td>Universally unique identifier</td>
 * <td>{@link java.util.UUID}</td>
 * <td></td></tr>
 *
 * </table>
 *
 * <p>Jakarta Data providers might allow additional entity attribute types.</p>
 *
 * <h2>Additional Method Parameters</h2>
 *
 * <p>When using {@code @Query} or the <i>Query By Method Name</i> pattern or
 * the <i>parameter-based automatic query method</i> pattern, after conditions
 * are determined from the corresponding parameters, the remaining repository
 * method parameters are used to enable other capabilities such as pagination,
 * limits, and sorting.</p>
 *
 * <h3>Limits</h3>
 *
 * <p>The number of results returned by a single invocation of a repository
 * find method may be limited by adding a parameter of type {@link Limit}.
 * The results may even be limited to a positioned range. For example,</p>
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
 * <p>A repository find method with a parameter of type {@link PageRequest}
 * allows its results to be split and retrieved in pages. For example,</p>
 *
 * <pre>
 * Product[] findByNameLikeOrderByAmountSoldDescNameAsc(
 *           String pattern, {@code PageRequest<Product>} pageRequest);
 * ...
 * page1 = products.findByNameLikeOrderByAmountSoldDescNameAsc(
 *                  "%phone%", PageRequest.of(Product.class).size(20));
 * </pre>
 *
 * <h3>Sorting at Runtime</h3>
 *
 * <p>When a page is requested with a {@code PageRequest}, dynamic sorting
 * criteria may be supplied via the method {@link PageRequest#sortBy(Sort)}
 * and its overloads. For example,</p>
 *
 * <pre>
 * Product[] findByNameLike(String pattern, {@code PageRequest<Product>} pagination);
 *
 * ...
 * {@code PageRequest<Product>} page1Request = PageRequest.of(Product.class)
 *                                                .size(25)
 *                                                .sortBy(Sort.desc("price"),
 *                                                        Sort.asc("name"));
 * page1 = products.findByNameLikeAndPriceBetween(
 *                 namePattern, minPrice, maxPrice, page1Request);
 * </pre>
 *
 * <p>An alternative when using the {@link StaticMetamodel} is to obtain the
 * page request from an {@link Order} instance, as follows,</p>
 *
 * <pre>
 * {@code PageRequest<Product>} pageRequest = Order.by(_Product.price.desc(),
 *                                             _Product.name.asc())
 *                                         .pageSize(25));
 * </pre>
 *
 * <p>To supply sort criteria dynamically without using pagination, an
 * instance of {@link Order} may be populated with one or more instances
 * of {@link Sort} and passed to the repository find method. For example,</p>
 *
 * <pre>
 * Product[] findByNameLike(String pattern, Limit max, {@code Order<Product>} sortBy);
 *
 * ...
 * found = products.findByNameLike(namePattern, Limit.of(25), Order.by(
 *                                 Sort.desc("price"),
 *                                 Sort.desc("amountSold"),
 *                                 Sort.asc("name")));
 * </pre>
 *
 * <p>Generic, untyped {@link Sort} criteria can be supplied directly to a
 * repository method with a variable arguments {@code Sort<?>...} parameter.
 * For example,</p>
 *
 * <pre>
 * Product[] findByNameLike(String pattern, Limit max, {@code Sort<?>...} sortBy);
 *
 * ...
 * found = products.findByNameLike(namePattern, Limit.of(25),
 *                                 Sort.desc("price"),
 *                                 Sort.desc("amountSold"),
 *                                 Sort.asc("name"));
 * </pre>
 *
 * <h2>Repository Default Methods</h2>
 *
 * <p>A repository interface may declare any number of {@code default} methods
 * with user-written implementations.</p>
 *
 * <h2>Resource Accessor Methods</h2>
 *
 * <p>In advanced scenarios, the application program might make direct use of
 * some underlying resource acquired by the Jakarta Data provider, such as a
 * {@code javax.sql.DataSource}, {@code java.sql.Connection}, or even an
 * {@code jakarta.persistence.EntityManager}.</p>
 *
 * <p>To expose access to an instance of such a resource, the repository
 * interface may declare an accessor method, a method with no parameters
 * whose return type is the type of the resource, for example, one of the
 * types listed above. When this method is called, the Jakarta Data provider
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
 * <p>If the resource type inherits from {@link AutoCloseable} and the
 * accessor method is called from within an invocation of a default method
 * of the repository, the Jakarta Data provider automatically closes the
 * resource after the invocation of the default method ends. On the other
 * hand, if the accessor method is called from outside the scope of a
 * default method of the repository, it is not automatically closed, and
 * the application programmer is responsible for closing the resource
 * instance.</p>
 *
 * <h2>Precedence of Repository Methods</h2>
 *
 * <p>The following order, with the lower number having higher precedence,
 * is used to interpret the meaning of repository methods.</p>
 *
 * <ol>
 * <li>If a method is a <b>Java default method</b> with implementation,
 *     provided, then the provided implementation is used.</li>
 * <li>If a method has a <b>Resource Accessor Method</b> return type,
 *     then the method is implemented as a Resource Accessor Method.</li>
 * <li>If a method is annotated with {@link Query}, then the method is
 *     implemented to run the corresponding Query Language query.</li>
 * <li>If a method is annotated with any annotation specifying the type
 *     of operation to be performed, for example, {@link Find}, or a
 *     lifecycle annotation such as {@link Insert}, {@link Update},
 *     {@link Save}, or {@link Delete}, then the annotation determines
 *     how the method is implemented, along with any data access related
 *     annotations placed on the method or on its parameters.</li>
 * <li>If a method is named according to the <b>Query by Method Name</b>
 *     naming conventions, then the implementation follows the Query by
 *     Method Name pattern.</li>
 * </ol>
 *
 * <p>A repository method which does not fit any of the listed patterns
 * and is not handled as a vendor-specific extension must either cause
 * an error at build time or raise {@link UnsupportedOperationException}
 * at runtime.</p>
 *
 * <h2>Identifying the type of Entity</h2>
 *
 * <p>Most repository methods perform operations related to a type of entity.
 * In some cases, the entity type is explicit within the signature of the
 * repository method, and in other cases, such as {@code countBy...} and
 * {@code existsBy...} the entity type cannot be determined from the method
 * signature and a primary entity type must be defined for the repository.</p>
 *
 * <b>Methods where the entity type is explicitly specified:</b>
 *
 * <ul>
 * <li>For repository methods annotated with {@link Insert}, {@link Update},
 *     {@link Save}, or {@link Delete} where the method parameter is a type,
 *     an array of type, or is parameterized with a type that is annotated
 *     as an entity, the entity type is determined by the method parameter
 *     type.</li>
 * <li>For find and delete methods where the return type is a type, an array
 *     of type, or is parameterized with a type that is annotated as an entity,
 *     such as {@code MyEntity}, {@code MyEntity[]}, or {@code Page<MyEntity>},
 *     the entity type is determined by the method return type.</li>
 * </ul>
 *
 * <b>Identifying a Primary Entity Type:</b>
 *
 * <p>The following precedence, from highest to lowest, is used to determine
 * a primary entity type for a repository.</p>
 *
 * <ol>
 * <li>The primary entity type for a repository interface may be specified
 * explicitly by having the repository interface inherit a superinterface
 * like {@link CrudRepository}, where the primary entity type is the argument
 * to the first type parameter of the superinterface. For example,
 * {@code Product}, in,
 * <pre>
 * &#64;Repository
 * public interface Products extends CrudRepository&lt;Product, Long&gt; {
 *     // applies to the primary entity type: Product
 *     int countByPriceLessThan(float max);
 * }
 * </pre>
 * </li>
 * <li>Otherwise, if the repository declares lifecycle methods&mdash;that is,
 * has methods annotated with a lifecycle annotation like {@link Insert},
 * {@link Update}, {@link Save}, or {@link Delete}, where the method
 * parameter is a type, an array of type, or is parameterized with a type
 * that is annotated as an entity&mdash;and all of these methods share the
 * same entity type, then the primary entity type for the repository is that
 * entity type. For example,
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
 *
 * <h2>Jakarta Interceptors and Repository Methods</h2>
 *
 * <p>A repository interface or method of a repository interface may be annotated with an
 * interceptor binding annotation. In the Jakarta EE environment, or in any other environment
 * where Jakarta Interceptors is available and integrated with Jakarta CDI, the repository
 * implementation is instantiated by the CDI bean container, and the interceptor binding type
 * is declared {@code @Inherited}, the interceptor binding annotation is inherited by the
 * repository implementation, and the interceptors bound to the annotation are applied
 * automatically by the implementation of Jakarta Interceptors.</p>
 *
 * <h2>Jakarta Transactions and Repository Methods</h2>
 *
 * <p>When Jakarta Transactions is available, repository methods can participate in global
 * transactions. If a global transaction is active on the thread of execution in which a
 * repository method is called, and the data source backing the repository is capable of
 * transaction enlistment, then the repository operation is performed within the context of
 * the global transaction.</p>
 *
 * <p>The repository operation must not not commit or roll back a transaction which was
 * already associated with the thread in which the repository operation was called, but it
 * might cause the transaction to be marked for rollback if the repository operation fails,
 * that is, it may set the transaction status to {@code Status.STATUS_MARKED_ROLLBACK}.</p>
 *
 * <p>A repository interface or method of a repository interface may be marked with the
 * annotation {@code jakarta.transaction.Transactional}. When a repository operation marked
 * {@code @Transactional} is called in an environment where both Jakarta Transactions and
 * Jakarta CDI are available, the semantics of this annotation are observed during execution
 * of the repository operation.</p>
 */
module jakarta.data {
    exports jakarta.data;
    exports jakarta.data.metamodel;
    exports jakarta.data.metamodel.impl;
    exports jakarta.data.page;
    exports jakarta.data.page.impl;
    exports jakarta.data.repository;
    exports jakarta.data.exceptions;
    opens jakarta.data.repository;
}