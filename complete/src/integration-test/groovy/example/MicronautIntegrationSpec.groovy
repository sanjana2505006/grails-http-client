package example

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import io.micronaut.context.ApplicationContext as MicronautApplicationContext
import spock.lang.IgnoreIf
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

// micronaut-core's ScopedValues references java.lang.ScopedValue.CallableOp,
// which only exists in JDK 25+ (JEP 506). Skip on older JDKs.
@IgnoreIf({ Runtime.version().feature() < 25 })
@Integration
@Rollback
class MicronautIntegrationSpec extends Specification {

    @Autowired
    MicronautApplicationContext micronautContext

    void 'declarative ItunesClient is registered as a Micronaut bean'() {
        when:
        def client = micronautContext.getBean(ItunesClient)

        then:
        client != null
        client instanceof ItunesClient
    }
}
