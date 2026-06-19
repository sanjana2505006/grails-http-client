package example

import groovy.transform.CompileStatic
import io.micronaut.serde.annotation.Serdeable

@CompileStatic
@Serdeable
class SearchResult {
    int resultCount
    List<Album> results = []
}
