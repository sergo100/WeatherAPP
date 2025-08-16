// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Этот файл обычно остается очень простым в современных проектах.
// Большая часть конфигурации (pluginManagement, dependencyResolutionManagement)
// теперь находится в settings.gradle.kts

// ЗАКОММЕНТИРОВАНО: Этот блок 'tasks.register("clean", Delete::class) { ... }' обычно не нужен
// в современных проектах, так как 'gradle clean' работает по умолчанию.
// Если вы хотите его оставить, убедитесь, что Gradle знает о классе Delete.
// Для упрощения, обычно его удаляют.
// tasks.register("clean", Delete::class) {
//     delete(rootProject.buildDir)
// }
