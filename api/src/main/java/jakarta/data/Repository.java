/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a data repository interface that will be implemented by the container/runtime.<p>
 *
 * This class is a CDI bean-defining annotation when CDI is available,
 * enabling the container/runtime to the implementation available via the
 * {@link jakarta.inject.Inject} annotation.<p>
 *
 * For example,<p>
 *
 * <pre>
 * &#64;Repository
 * public interface Products extends DataRepository<Product, Long> {
 *     List&lt;Product&gt; findByNameLike(String namePattern);
 *     ...
 * </pre>
 *
 * <pre>
 * &#64;Inject
 * Products products;
 *
 * ...
 * found = products.findByNameLike("%Printer%");
 * </pre>
 *
 * TODO Also show Query(...) annotation example if that gets brought over?
 *
 * Methods of repository interfaces must be styled according to a
 * defined set of conventions, which instruct the container/runtime
 * about the desired data access operation to perform.
 * These conventions consist of
 * patterns of reserved keywords within the method name, as well as
 * annotations that are placed upon the method and its parameters.<p>
 *
 * Built-in repository interfaces such as {@link DataRepository}
 * provide a base set of predefined repository methods
 * which serve as an optional starting point.
 * You can extend these built-in interfaces to add your own custom methods.
 * You can also copy individual method signatures from the
 * built-in repository methods onto your own, which is possible
 * because the built-in repository methods are consistent with the
 * same set of conventions that you use to write custom repository methods.<p>
 *
 * <h3>Reserved Keywords for Name-Pattern-Based Repository Methods</h3>
 *
 * TODO What follows will neither be correct nor complete in this initial issue/pull.
 * Its purpose is to figure out a structure into which we can document the
 * actual patterns and details that we come up with when consensus is established.
 * For now, I've just take some common aspects from existing solutions and
 * tried to see if I could fit in the sort of detail that I'd want to see
 * as a user. Some of that actual detail is very likely to be wrong, but
 * that's good because it will lead to the opening of issues for
 * clarifications and discussions to come up with a more comprehensive
 * and better documented solution.
 *
 * <table>
 * <caption><b>Reserved Method Name Prefixes</b></caption>
 * <tr>
 * <td valign="top"><b>Prefix</b></td>
 * <td valign="top"><b>Description</b></td>
 * <td valign="top"><b>Example</b></td>
 * </tr>
 *
 * <tr valign="top"><td><code>countBy</code> or just <code>count</code>?</td>
 * <td>counts the number of entities</td>
 * <td><code>countByAgeGreaterThanEqual(ageLimit)</code></td></tr>
 *
 * <tr valign="top"><td><code>deleteBy</code></td>
 * <td>for delete operations</td>
 * <td><code>deleteByStatus("DISCONTINUED")</code></td></tr>
 *
 * <tr valign="top"><td><code>existsBy</code></td>
 * <td>for determining existence</td>
 * <td><code>existsByYearHiredAndWageLessThan(2022, 60000)</code></td></tr>
 *
 * <tr valign="top"><td><code>findBy</code></td>
 * <td>for find operations</td>
 * <td><code>findByHeightBetween(minHeight, maxHeight)</code></td></tr>
 *
 * <tr valign="top"><td><code>save</code></td>
 * <td>for save operations</td>
 * <td><code>save(Product newProduct)</code></td></tr>
 *
 * <tr valign="top"><td><code>updateBy</code></td>
 * <td>TODO Is there a reason existing solutions don't seem to have this?</td>
 * <td><code>updateByIdSetModifiedOnAddPrice(productId, now, 10.0)</code></td></tr>
 * </table>
 * TODO When can "By" be omitted and "All" added? Need to document that.
 * <p>
 *
 * <table>
 * <caption><b>Reserved Keywords</b></caption>
 * <tr>
 * <td valign="top"><b>Keyword</b></td>
 * <td valign="top"><b>Applies to</b></td>
 * <td valign="top"><b>Description</b></td>
 * <td valign="top"><b>Example</b></td>
 * </tr>
 *
 * <tr valign="top"><td><code>And</code></td>
 * <td>conditions</td>
 * <td>Requires both conditions to be satisfied in order to match an entity.
 * Precedence is determined by the data access provider.
 * TODO Or should we enforce a particular precedence?</td>
 * <td><code>findByNameLikeAndPriceLessThanEqual(namePattern, maxPrice)</code></td></tr>
 *
 * <tr valign="top"><td><code>Asc</code></td>
 * <td>sorting</td>
 * <td>Specifies ascending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAgeOrderByFirstNameAsc(age)</code></td></tr>
 *
 * <tr valign="top"><td><code>Between</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be within the range specified by two parameters.
 * The minimum is listed first, then the maximum.</td>
 * <td><code>findByAgeBetween(minAge, maxAge)</code></td></tr>
 *
 * <tr valign="top"><td><code>Contains</code></td>
 * <td>collections, strings</td>
 * <td> TODO Need to determine if I have a proper understanding of this one.
 * For Collection attributes, requires that the entity's attribute value,
 * which is a collection, includes the parameter value.
 * For String attributes, requires that any substring of the entity's attribute value
 * match the entity's attribute value. which can be a pattern</td>
 * <td><code>findByRecipientsContains(email)</code>
 * <br><code>findByDescriptionNotContains("refurbished")</code></td></tr>
 *
 * <tr valign="top"><td><code>Desc</code></td>
 * <td>sorting</td>
 * <td>Specifies descending sort order for <code>findBy</code> queries</td>
 * <td><code>findByAuthorLastNameOrderByYearPublishedDesc(surname)</code></td></tr>
 *
 * <tr valign="top"><td><code>EndsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the end of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameEndsWith(surname)</code></td></tr>
 *
 * <tr valign="top"><td><code>GreaterThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be larger than the parameter value.</td>
 * <td><code>findByStartTimeGreaterThan(startedAfter)</code></td></tr>
 *
 * <tr valign="top"><td><code>GreaterThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as big as the parameter value.</td>
 * <td><code>findByAgeGreaterThanEqual(minimumAge)</code></td></tr>
 *
 * <tr valign="top"><td><code>In</code></td>
 * <td>all attribute types</td>
 * <td>Requires that the entity's attribute value be within the list that is the parameter value.</td>
 * <td><code>findByNameIn(names)</code></td></tr>
 *
 * <tr valign="top"><td><code>LessThan</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be less than the parameter value.</td>
 * <td><code>findByStartTimeLessThan(startedBefore)</code></td></tr>
 *
 * <tr valign="top"><td><code>LessThanEqual</code></td>
 * <td>numeric, strings, time</td>
 * <td>Requires that the entity's attribute value be at least as small as the parameter value.</td>
 * <td><code>findByAgeLessThanEqual(maximumAge)</code></td></tr>
 *
 * <tr valign="top"><td><code>Like</code></td>
 * <td>strings</td>
 * <td>Requires that the entity's attribute value match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameLike(namePattern)</code></td></tr>
 *
 * <tr valign="top"><td><code>Not</code></td>
 * <td>condition</td>
 * <td>Negates a condition.</td>
 * <td><code>deleteByNameNotLike(namePattern)</code>
 * <br><code>findByStatusNot("RUNNING")</code></td></tr>
 *
 * <tr valign="top"><td><code>Or</code></td>
 * <td>conditions</td>
 * <td>Requires at least one of the two conditions to be satisfied in order to match an entity.
 * Precedence is determined by the data access provider.
 * TODO Or should we enforce a particular precedence?</td>
 * <td><code>findByPriceLessThanEqualOrDiscountGreaterThanEqual(maxPrice, minDiscount)</code></td></tr>
 *
 * <tr valign="top"><td><code>OrderBy</code></td>
 * <td>sorting</td>
 * <td>Sorts results of a <code>findBy</code> query according to one or more entity attributes.
 * Multiple attributes are delimited by <code>Asc</code> and <code>Desc</code>,
 * which indicate ascending and descending sort direction.
 * Precedence in sorting is determined by the order in which attributes are listed.
 * TODO could OrderBy be specified annotatively instead?</td>
 * <td><code>findByStatusOrderByYearHiredDescLastNameAsc(empStatus)</code></td></tr>
 *
 * <tr valign="top"><td><code>StartsWith</code></td>
 * <td>strings</td>
 * <td>Requires that the characters at the beginning of the entity's attribute value
 * match the parameter value, which can be a pattern.</td>
 * <td><code>findByNameStartsWith(firstTwoLetters)</code></td></tr>
 *
 * </table>
 * Wildcard characters for patterns are determined by the data access provider.
 * For Jakarta Persistence providers, <code>_</code> matches any one character
 * and <code>%</code> matches 0 or more characters.
 * TODO Does Jakarta NoSQL have the same or different wildcard characters?
 * <p>
 *
 * TODO The above list of reserved words is nowhere near complete.
 * Hopefully it was at least varied enough to illustrate the sort of information to document.
 *
 * TODO The following reflects my own guessing about what return types could make sense.
 * Many are missed, including for reactive.<p>
 *
 * <table>
 * <caption><b>Return Types for Repository Methods</b></caption>
 * <tr>
 * <td valign="top"><b>Method</b></td>
 * <td valign="top"><b>Return Types</b></td>
 * <td valign="top"><b>Notes</b></td>
 * </tr>
 *
 * <tr valign="top"><td><code>countBy...</code></td>
 * <td><code>long</code>, <code>Long</code>,
 * <br><code>int</code>, <code>Integer</code>,
 * <br><code>short</code>, <code>Short</code>,
 * <br><code>Number</code></td>
 * <td>Jakarta Persistence providers limit the maximum to <code>Integer.MAX_VALUE</code></td></tr>
 *
 * <tr valign="top"><td><code>deleteBy...</code>,
 * <br><code>updateBy...</code></td>
 * <td><code>void</code>, <code>Void</code>,
 * <br><code>boolean</code>, <code>Boolean</code>,
 * <br><code>long</code>, <code>Long</code>,
 * <br><code>int</code>, <code>Integer</code>,
 * <br><code>short</code>, <code>Short</code>,
 * <br><code>Number</code></td>
 * <td>Jakarta Persistence providers limit the maximum to <code>Integer.MAX_VALUE</code></td></tr>
 *
 * <tr valign="top"><td><code>existsBy...</code></td>
 * <td><code>boolean</code>, <code>Boolean</code></td>
 * <td></td></tr>
 *
 * <tr valign="top"><td><code>findBy...</code></td>
 * <td><code>E</code>,
 * <br><code>Optional&lt;E&gt;</code></td>
 * <td>For queries returning a single item (or none)</td></tr>
 *
 * <tr valign="top"><td><code>findBy...</code></td>
 * <td><code>E[]</code>,
 * <br><code>Iterable&lt;E&gt;</code>,
 * <br><code>Stream&lt;E&gt;</code>,
 * <br><code>Collection&lt;E&gt;</code></td>
 * <td></td></tr>
 *
 * <tr valign="top"><td><code>findBy...</code></td>
 * <td><code>Collection</code> subtypes</td>
 * <td>The subtype must have a public default constructor and support <code>addAll</code> or <code>add</code></td></tr>
 *
 * <tr valign="top"><td><code>findBy...</code></td>
 * <td><code>Page&lt;E&gt;</code>, <code>Iterator&lt;E&gt;</code></td>
 * <td>For use with pagination</td></tr>
 *
 * <tr valign="top"><td><code>findBy...</code></td>
 * <td><code>LinkedHashMap&lt;K, E&gt;</code></td>
 * <td>Ordered map of Id attribute value to entity</td></tr>
 *
 * <tr valign="top"><td><code>save(E)</code></td>
 * <td><code>E</code>,
 * <br><code>void</code>, <code>Void</code></td>
 * <td>For saving a single entity.</td></tr>
 *
 * <tr valign="top"><td><code>save(E...)</code>,
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
 * <p>
 *
 * <h3>Parameters to Repository Methods</h3>
 *
 * The parameters to a repository method correspond to the conditions that are
 * defined within the name of repository method (see reserved keywords above),
 * in the same order specified.
 * Most conditions, such as <code>Like</code> or <code>LessThan</code>,
 * correspond to a single method parameter. The exception to this rule is
 * <code>Between</code>, which corresponds to two method parameters.<p>
 *
 * After all conditions are matched up with the corresponding parameters,
 * the remaining repository method parameters are used to enable other
 * capabilities such as pagination and sorting.<p>
 *
 * TODO write this section after pagination and sorting and other aspects
 * are discussed and defined.<p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {
}
