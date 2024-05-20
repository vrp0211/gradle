/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.configurationcache.isolated

class IsolatedProjectsAccessFromKotlinDslIntegrationTest extends AbstractIsolatedProjectsIntegrationTest {
    def "reports problem when build script uses #block block to apply plugins to another project"() {
        createDirs("a", "b")
        settingsFile << """
            include("a")
            include("b")
        """
        buildKotlinFile << """
            $block {
                plugins.apply("java-library")
            }
        """

        when:
        isolatedProjectsFails("assemble")

        then:
        fixture.assertStateStoredAndDiscarded {
            projectsConfigured(":", ":a", ":b")
            problem("Build file 'build.gradle.kts': Project ':' cannot access 'Project.plugins' functionality on $message", 2)
        }

        where:
        block         | message
        "allprojects" | "subprojects via 'allprojects'"
        "subprojects" | "subprojects"
    }

    def "reports problem when build script uses #block block to access dynamically added elements"() {
        createDirs("a", "b")
        settingsFile << """
            include("a")
            include("b")
        """
        buildKotlinFile << """
            plugins { id("java-library") }
            $block {
                plugins.apply("java-library")
                java { }
                java.sourceCompatibility
            }
        """

        when:
        isolatedProjectsFails("assemble")

        then:
        fixture.assertStateStoredAndDiscarded {
            projectsConfigured(":", ":a", ":b")
            problem("Build file 'build.gradle.kts': Project ':' cannot access 'Project.extensions' functionality on $message", 3)
            problem("Build file 'build.gradle.kts': Project ':' cannot access 'Project.plugins' functionality on $message", 3)
        }

        where:
        block         | message
        "allprojects" | "subprojects via 'allprojects'"
        "subprojects" | "subprojects"
    }

    def "reports cross-project model access in Gradle.#invocation"() {
        createDirs("a", "b")
        settingsFile << """
            include("a")
            include("b")
        """
        file("a/build.gradle.kts") << """
            gradle.${invocation} { println(buildDir) }
        """

        when:
        isolatedProjectsFails(":a:help", ":b:help")

        then:
        fixture.assertStateStoredAndDiscarded {
            projectsConfigured(":", ":a", ":b")
            problem("Build file 'a/build.gradle.kts': Project ':a' cannot access 'Project.buildDir' functionality on another project ':b'")
        }

        where:
        invocation               | accessedProjects
        "beforeProject"          | [":b"]
        "afterProject"           | [":b"]
    }

    def "reports cross-project model access from a listener added to Gradle.projectsEvaluated"() {
        createDirs("a", "b")
        settingsFile << """
            include("a")
            include("b")
        """
        file("a/build.gradle.kts") << """
            gradle.projectsEvaluated {
                allprojects { println(buildDir) }
            }
        """

        when:
        isolatedProjectsFails(":a:help", ":b:help")

        then:
        fixture.assertStateStoredAndDiscarded {
            projectsConfigured(":", ":a", ":b")
            problem("Build file 'a/build.gradle.kts': Project ':a' cannot access 'Project.buildDir' functionality on subprojects of project ':'", 2)
        }
    }

    def "build script can query basic details of projects in a function called from allprojects block"() {
        createDirs("a", "b")
        settingsFile << """
            rootProject.name = "root"
            include("a", "b")
        """
        buildKotlinFile << """
            fun printInfo(p: Project) {
                println("project name = " + p.name)
            }

            allprojects {
                printInfo(project)
            }

            tasks.register("something")
        """

        when:
        isolatedProjectsRun("something")

        then:
        outputContains("project name = root")
        outputContains("project name = a")
        outputContains("project name = b")

        fixture.assertStateStored {
            projectsConfigured(":", ":a", ":b")
        }
    }
}
