package pl.szpp.backend;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = ProjectArchitectureTest.PROJECT_ROOT_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
public class ProjectArchitectureTest {

    public static final String PROJECT_ROOT_PACKAGE = "pl.szpp.backend";

    @ArchTest
    ArchRule mainSubpackagesShouldNotHaveDependencyCycles =
        slices().matching("..backend.(*)..")
            .should().beFreeOfCycles();

}
