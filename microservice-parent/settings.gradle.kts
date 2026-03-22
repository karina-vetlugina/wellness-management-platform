//pluginManagement {
//    repositories {
//        mavenCentral()
//        gradlePluginPortal()
//    }
//}
//
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        mavenCentral()
//    }
//}

rootProject.name = "microservice-parent"
include("wellness-service")
include("event-service")
include("goal-tracking-service")
include("api-gateway")


include("shared-schema")