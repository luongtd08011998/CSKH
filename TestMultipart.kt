import io.ktor.http.ContentDisposition
fun main() {
    val cd = ContentDisposition.File
        .withParameter(ContentDisposition.Parameters.Name, "images")
        .withParameter(ContentDisposition.Parameters.FileName, "test.jpg")
    println(cd.toString())
}
