package ee.jakarta.tck.data.metadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;

/**
 * This is a utility class that will analyze the TCK and generate documentation for the following information: 
 * 1. The name of tests that are disabled (due to challenges)
 * 2. The number of tests that need to pass for certification
 * 3. Expected JUnit output
 * 4. Expected Signature test output
 * 
 * This will be run automatically each time the tck-dist module is built.
 * Meaning that we don't need to update our doc each time we add/remove/disable a test. 
 * 
 * Files are output to: 
 *  ${project.basedir}/src/main/asciidoc/generated/
 *  
 * File names are:
 *  expected-output.adoc
 *  expected-sig-output.adoc
 *  runtime-tests.adoc
 *  successful-challenges.adoc
 */
public class CollectMetaData {
    // Constants
    private static final String FRAMEWORK_PACKAGE_PREFIX = "ee/jakarta/tck/data/framework";
    
    private static final String RUNTIME_TESTS_FILE = "runtime-tests.adoc";
    private static final String CHALLENGED_TESTS_FILE = "successful-challenges.adoc";
    private static final String SIG_OUTPUT_FILE = "expected-sig-output.adoc";
    private static final String EXPECTED_OUTPUT_FILE = "expected-output.adoc";
        
    // Data holders
    private static boolean debug = false;
    private static List<String> apiPackages;
    private static List<Class<?>> testClasses;
    private static File adocGeneratedLocation;
    
    private CollectMetaData() {
        //Do nothing
    }
    
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            throw new RuntimeException("CollectMetaData expected exactly 3 arguments [debug, path-to-tck, output-file-location]");
        }
        
        //Load arguments
        debug = Boolean.valueOf(args[0]);
        testClasses = getClassNames(args[1]);
        adocGeneratedLocation = new File(args[2]);
        
        //Check asciidoctor generated folder exists
        if(!adocGeneratedLocation.exists()) {
            adocGeneratedLocation.mkdirs();
        }
        
        //Collect test metadata
        List<TestMetaData> testMetaData = collectMetaData(testClasses);
        
        //Write the generated asciidoc files
        writeTestCounts(testMetaData, new File(adocGeneratedLocation, RUNTIME_TESTS_FILE));
        writeSuccessfulChallenges(testMetaData, new File(adocGeneratedLocation, CHALLENGED_TESTS_FILE));
        writeSigOutput(apiPackages, new File(adocGeneratedLocation, SIG_OUTPUT_FILE));
        writeOutput(testMetaData, new File(adocGeneratedLocation, EXPECTED_OUTPUT_FILE));
        writeGitIgnore(new File(adocGeneratedLocation, ".gitignore"), RUNTIME_TESTS_FILE, CHALLENGED_TESTS_FILE, SIG_OUTPUT_FILE, EXPECTED_OUTPUT_FILE);
    }

    /**
     * Writes generated files to a .gitignore file
     * 
     * @param outputLocation - the output file
     * @param ignoredFiles   - files to ignore 
     * @throws IOException   - exception if we cannot write to this location
     */
    private static void writeGitIgnore(File outputLocation, String... ignoredFiles) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))) {
            for(String ignoredFile : ignoredFiles) {
                writer.write(ignoredFile + System.lineSeparator());
            }
        }
    }
    
    /**
     * Writes example output to the generated adoc folder
     * 
     * @param testMetaData   - the test metadata we previously collected
     * @param outputLocation - the output file
     * @throws IOException   - exception if we cannot write to this location
     */
    private static void writeOutput(List<TestMetaData> testMetaData, File outputLocation) throws IOException {
        //TODO replace with testblock if we end up using Java 17
        StringBufferWrapper expectOutputSection = new StringBufferWrapper();
        
        expectOutputSection.appendNewLine("[source, txt]");
        expectOutputSection.appendNewLine("----");
        expectOutputSection.appendNewLine("$ mvn clean test");
        expectOutputSection.appendNewLine("...");
        
        expectOutputSection.appendNewLine("[INFO] --- maven-surefire-plugin:3.0.0-M7:test (default-test) @ tck.runner ---");
        expectOutputSection.appendNewLine("[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider");
        expectOutputSection.appendNewLine("[INFO]");
        
        expectOutputSection.appendNewLine("[INFO] -------------------------------------------------------");
        expectOutputSection.appendNewLine("[INFO]  T E S T S");
        expectOutputSection.appendNewLine("[INFO] -------------------------------------------------------");
        
        for(String testClass : testMetaData.stream().map(metaData -> metaData.testClass).distinct().collect(Collectors.toList())) {
            List<TestMetaData> theseTests = testMetaData.stream().filter(metaData -> metaData.testClass == testClass).collect(Collectors.toList());
            long testCount = theseTests.stream().filter(metaData -> !metaData.isDisabled).count();
            long disabledCount = theseTests.stream().filter(metaData -> metaData.isDisabled).count();
            expectOutputSection.appendNewLine("[INFO] Running " + testClass);
            
            if(disabledCount > 0) {
                expectOutputSection.append("[WARNING] Tests run: " + testCount + ", Failures: 0, Errors: 0, Skipped: " + disabledCount + ",");
            } else {
                expectOutputSection.append("[INFO] Tests run: " + testCount + ", Failures: 0, Errors: 0, Skipped: " + disabledCount + ",");    
            }
            
            expectOutputSection.appendNewLine("Time elapsed: y.yy s - in " +  testClass);
            expectOutputSection.appendNewLine("[INFO]");
        }
        
        expectOutputSection.appendNewLine("[INFO] Results:");
        expectOutputSection.appendNewLine("[INFO]");
        
        long totalTestCount = testMetaData.stream().filter(metaData -> !metaData.isDisabled).count();
        long totalDisabledCount = testMetaData.stream().filter(metaData -> metaData.isDisabled).count();
        
        if(totalDisabledCount > 0) {
            expectOutputSection.appendNewLine("[WARNING] Tests run: " + totalTestCount + ", Failures: 0, Errors: 0, Skipped: " + totalDisabledCount);    
        } else {
            expectOutputSection.appendNewLine("[INFO] Tests run: " + totalTestCount + ", Failures: 0, Errors: 0, Skipped: " + totalDisabledCount);    
        }
        
        expectOutputSection.appendNewLine("[INFO]");
        expectOutputSection.appendNewLine("[INFO] -------------------------------------------------------");
        expectOutputSection.appendNewLine("[INFO] BUILD SUCCESS");
        expectOutputSection.appendNewLine("[INFO] -------------------------------------------------------");
        expectOutputSection.appendNewLine("[INFO] Total time:  xx.xxx s");
        expectOutputSection.appendNewLine("[INFO] Finished at: " + LocalDateTime.now().toString());
        expectOutputSection.appendNewLine("[INFO] -------------------------------------------------------");
        expectOutputSection.append("----");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))) {
            writer.write(expectOutputSection.toString());
        }
    }
    
    /**
     * Writes example signature test output to the generated adoc folder
     * 
     * @param apiPackages    - the API packages used by the signature test
     * @param outputLocation - the output file
     * @throws IOException   - exception if we cannot write to this location
     */
    private static void writeSigOutput(List<String> apiPackages, File outputLocation) throws IOException {
        //TODO replace with testblock if we end up using Java 17
        StringBufferWrapper expectSigOutputSection = new StringBufferWrapper();
        
        expectSigOutputSection.appendNewLine("[source, txt]");
        expectSigOutputSection.appendNewLine("----");
        expectSigOutputSection.appendNewLine("******************************************************");
        expectSigOutputSection.appendNewLine("All package signatures passed.");
        expectSigOutputSection.appendNewLine("\tPassed packages listed below:");
        
        for(String apiPackage : apiPackages) {
            expectSigOutputSection.appendNewLine("\t\t" + apiPackage + "(static mode)");
            expectSigOutputSection.appendNewLine("\t\t" + apiPackage + "(reflection mode)");
        }
        
        expectSigOutputSection.appendNewLine("******************************************************");
        expectSigOutputSection.appendNewLine("----");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))) {
            writer.write(expectSigOutputSection.toString());
        } 
    }

    /**
     * Writes successful challenges to the generated adoc folder
     * 
     * @param testMetaData   - the test metadata we previously collected
     * @param outputLocation - the output file
     * @throws IOException   - exception if we cannot write to this location
     */
    private static void writeSuccessfulChallenges(List<TestMetaData> testMetaData, File outputLocation) throws IOException {
        //TODO replace with testblock if we end up using Java 17
        StringBufferWrapper exemptTestsSection = new StringBufferWrapper();

        exemptTestsSection.appendNewLine("[cols=\"1,1,1\"]" );
        exemptTestsSection.appendNewLine("|===" );
        exemptTestsSection.appendNewParagraph("|Class |Method |Reason"  );
        testMetaData.stream().filter(TestMetaData::isDisabled).forEach(test -> {
            exemptTestsSection.appendNewLine("|" + test.testClass );
            exemptTestsSection.appendNewLine("|" + test.testName );
            exemptTestsSection.appendNewLine("|" + test.disabledText );
            exemptTestsSection.appendNewLine("");
        });
        exemptTestsSection.append("|===");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))) {
            writer.write(exemptTestsSection.toString() );
        }
        
    }

    /**
     * Writes expected test counts output to the generated adoc folder
     * 
     * @param testMetaData   - the test metadata we previously collected
     * @param outputLocation - the output file
     * @throws IOException   - exception if we cannot write to this location
     */
    private static void writeTestCounts(List<TestMetaData> testMetaData, File outputLocation) throws IOException {
        List<TestMetaData> runnableTestMetaData = testMetaData.stream().filter(TestMetaData::isRunnable).collect(Collectors.toList());
        final String compatabilityStatement = "* tests must be passed to successfully claim $1 compatibility.";
        
        StringBufferWrapper runtimeTestsSection = new StringBufferWrapper();
        
        //TODO replace with testblock if we end up using Java 17
        runtimeTestsSection.append("* *" + runnableTestMetaData.stream().filter(TestMetaData::isStandalone).count());
        runtimeTestsSection.appendNewLine(compatabilityStatement.replace("$1", "standalone"));
        runtimeTestsSection.append("* *" + runnableTestMetaData.stream().filter(TestMetaData::isCore).count());
        runtimeTestsSection.appendNewLine(compatabilityStatement.replace("$1", "core"));
        runtimeTestsSection.append("* *" + runnableTestMetaData.stream().filter(TestMetaData::isWeb).count());
        runtimeTestsSection.appendNewLine(compatabilityStatement.replace("$1", "web"));
        runtimeTestsSection.append("* *" + runnableTestMetaData.stream().filter(TestMetaData::isFull).count());
        runtimeTestsSection.appendNewParagraph(compatabilityStatement.replace("$1", "full"));
        runtimeTestsSection.append("*Note:* This count includes the signature test, but does not include disabled tests");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))) {
            writer.write(runtimeTestsSection.toString());
        }
    }
    
    /**
     * Inspects each class for methods and annotations and constructs a metadata object.
     * Collects all metadata objects and returns them as a list. 
     * 
     * @param testClasses - the test classes
     * @return list of metadata for each test
     */
    private static List<TestMetaData> collectMetaData(List<Class<?>> testClasses) {
        return testClasses.stream()
                .flatMap(clazz -> AnnotationSupport.findAnnotatedMethods(clazz, Assertion.class, HierarchyTraversalMode.TOP_DOWN).stream())
                .map(method -> {
                    TestMetaData metaData = new TestMetaData();
                    metaData.testClass = method.getDeclaringClass().getCanonicalName();
                    metaData.testName = method.getName();
                    metaData.assertion = method.getAnnotation(Assertion.class).strategy();
                    metaData.isDisabled = method.isAnnotationPresent(Disabled.class);
                    metaData.disabledText = metaData.isDisabled ? method.getAnnotation(Disabled.class).value() : "";
                    metaData.tags = findTags(method.getDeclaringClass());
                    return metaData;
                }).collect(Collectors.toList());
    }
    
    /**
     * Finds the tag(s) on a test class which could be a {@Tag} or {@Tags} annotation.
     * 
     * @param clazz - the test class
     * @return - a list of tag values on this class 
     */
    private static List<String> findTags(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations())
        // Get 1st level nested annotations
        .flatMap(anno -> Arrays.stream(anno.annotationType().getAnnotations()))
        // Get all tag annotations
        .flatMap(anno -> {
            if(anno instanceof Tag)
                return Stream.of((Tag) anno);
            if(anno instanceof Tags)
                return Arrays.stream(((Tags) anno).value());
            return Stream.empty();
        })
        .map(anno -> anno.value())
        .collect(Collectors.toList());

    }
    
    /**
     * Finds and loads all test classes inside of a TCK jar
     * 
     * @param jarLocation - Path to the TCK jar
     * @return List of test classes
     * @throws Exception - throws exception if jar cannot be located, or classes cannot be loaded.
     */
    private static List<Class<?>> getClassNames(String jarLocation) throws Exception {
        ArrayList<Class<?>> classList = new ArrayList<>();
        
        try (JarInputStream jar = new JarInputStream(new FileInputStream(jarLocation));) {
            for(JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
                if(isTestClass(entry.getName())) {
                    debug("Attempting to load test class: " + entry.getName());
                    classList.add(getClass(entry.getName().replaceAll("/", "\\.")));
                } else if(entry.getName().contains("sig-test-pkg-list.txt")) {
                    debug("Attempting to read package list" + entry.getName());
                    apiPackages = new String(jar.readAllBytes(), StandardCharsets.UTF_8).lines()
                        .filter(line -> !line.contains("#"))
                        .filter(line -> !line.isBlank())
                        .collect(Collectors.toList());
                    debug("apiPackages populated with: " + apiPackages.toString());
                }
                jar.closeEntry();
            }
        }
        
        return classList;
    }
    
    /**
     * Determines if a jar resource is a test class or not.
     * 
     * @param entryName - The fully qualified resource name
     * @return true - if resource is a test class, false otherwise
     */
    private static boolean isTestClass(String entryName) {
        if(! entryName.endsWith(".class") )
            return false;
        if(entryName.contains(FRAMEWORK_PACKAGE_PREFIX))
            return false;
        if(! entryName.substring(entryName.lastIndexOf("/"), entryName.lastIndexOf(".")).toLowerCase().contains("tests"))
            return false;
            
        return true;
    }
 
    /**
     * Loads a given class and returns it
     * 
     * @param className - The name of the class with or without the .class suffix
     * @return The class object
     */
    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Prints message if debugging is enabled
     * 
     * @param message - The message to print
     */
    private static void debug(String message) {
        if(debug)
            System.out.println(message);
    }
    
    /**
     * A data structure that represents data associated with test methods.
     */
    public static class TestMetaData {
        //TODO replace with record if we end up using Java 17
        private String testClass;
        private String testName;
        private String assertion;
        private boolean isDisabled;
        private String disabledText;
        private List<String> tags;
        
        @Override
        public String toString() {
            return "TestMetaData [testName=" + testName + ", assertion=" + assertion + ", isDisabled=" + isDisabled
                    + ", disabledText=" + disabledText + ", tags=" + tags + "]";
        }
        
        boolean isStandalone() {
            return tags.contains("standalone");
        }
        
        boolean isCore() {
            return tags.contains("core");
        }
        
        boolean isWeb() {
            return tags.contains("web");
        }
        
        boolean isFull() {
            return tags.contains("full");
        }
        
        boolean isDisabled() {
            return isDisabled;
        }
        
        boolean isRunnable() {
            return ! isDisabled;
        }
    }
    
    /**
     * Utility wrapper to append strings with new lines
     * to make construction of asciidoctor files easier
     */
    public static class StringBufferWrapper {
        private StringBuffer buf;
        
        private static final String nl = System.lineSeparator();
        
        public StringBufferWrapper() {
            buf = new StringBuffer();
        }
        
        public void append(String message) {
            buf.append(message);
        }
        
        public void appendNewLine(String message) {
            buf.append(message + nl);
        }
        
        public void appendNewParagraph(String message) {
            buf.append(message + nl + nl);
        }
        
        @Override
        public String toString() {
            return buf.toString();
        }
    }
}
