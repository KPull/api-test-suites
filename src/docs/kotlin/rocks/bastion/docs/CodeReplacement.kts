package rocks.bastion.docs

import java.io.File

import org.jetbrains.kotlin.maven.ExecuteKotlinScriptMojo

val mojo = ExecuteKotlinScriptMojo.INSTANCE

class Library(val sources: File, val boundary: Regex = Regex("// docs:([a-zA-Z0-9\\-_]+)")) {

    private val snippets = sources.walk().filter { isJavaOrGroovyFile(it) }
            .map { toCodeExampleSnippets(it) }
            .reduce { left, right -> left + right }

    private fun toCodeExampleSnippets(it: File): Map<String, String> {
        val text = it.readText()
        val matches = boundary.findAll(text)
        return matches.groupBy { delimeterKey(it) }
                .mapValues { toCodeContentBetweenDelimiters(it, text) }
    }

    private fun toCodeContentBetweenDelimiters(it: Map.Entry<String, List<MatchResult>>, text: String) = text.substring(it.value[0].range.last + 1, it.value[1].range.first).trimIndent()

    private fun delimeterKey(it: MatchResult) = it.groupValues[1]

    private fun isJavaOrGroovyFile(it: File) = it.name.endsWith(".java", true) || it.name.endsWith(".groovy")

    fun expand(key: String) = snippets[key]

}

class ReplacementChain(val source: File, val destination: File, val replacements: List<(String) -> String>) {

    fun replace(): Unit {
        val text = source.readText()
        val replacedText = replacements.fold(text) { operand, replacement ->
            replacement.invoke(operand)
        }
        destination.parentFile.mkdirs()
        destination.writeText(replacedText)
    }

}

fun replaceCodeExamples(library: Library, placeholder: Regex = Regex("\\[ex:([a-zA-Z0-9\\-_]+?)\\]")) =
        fun(text: String) = placeholder.replace(text) {
            library.expand(it.groupValues[1])!!
        }

fun replaceVersion(placeholder: Regex = Regex("\\{bastion-version\\}")) =
        fun(text: String) = placeholder.replace(text, mojo.project.version)

val library = Library(File(mojo.project.basedir, "src/test"))
val replacements = ReplacementChain(File(mojo.project.basedir, "src/docs/asciidoc/index.adoc"), File(mojo.project.basedir, "target/generated-docs/index.adoc"),
        listOf(replaceCodeExamples(library), replaceVersion()))
replacements.replace()