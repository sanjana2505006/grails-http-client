package example

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client('https://itunes.apple.com/')
interface ItunesClient {

    @Get('/search?limit=25&media=music&entity=album&term={term}')
    SearchResult search(String term)
}
