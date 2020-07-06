package pl.szpp.backend;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.quarkus.test.junit.QuarkusTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static pl.szpp.backend.ProjectArchitectureTest.PROJECT_ROOT_PACKAGE;

@AnalyzeClasses(packages = PROJECT_ROOT_PACKAGE)
class ProjectTestsArchitectureTest {

    @ArchTest
    ArchRule quarkusTestsShouldBeIntegrationTests =
        classes().that().areAnnotatedWith(QuarkusTest.class)
            .should().haveSimpleNameEndingWith("IT");

}
