package example

import groovy.transform.CompileStatic
import io.micronaut.serde.annotation.Serdeable

@CompileStatic
@Serdeable
class Album {
    String artistName
    String collectionName
    String collectionViewUrl
}
