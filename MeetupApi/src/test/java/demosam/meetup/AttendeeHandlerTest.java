package demosam.meetup;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true, services = {"dynamodb"})
public class AttendeeHandlerTest {
    private final static AmazonDynamoDB dynamoDB = DockerTestUtils.getClientDynamoDb();
    private final AttendeeHandler attendeeHandler = new AttendeeHandler(dynamoDB);

    @BeforeClass
    public static void setup() {
        dynamoDB.createTable(new CreateTableRequest()
                .withTableName("Attendees")
                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("email").withAttributeType(ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName("email"))
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L)));
    }

    @Test
    public void handleRequest() {
        Attendee attendeeToPut = new Attendee();
        attendeeToPut.setEmail("paul.smith@email.com");
        attendeeToPut.setName("Paul Smith");

        attendeeHandler.handleRequest(new APIGatewayProxyRequestEvent().withBody(new Gson().toJson(attendeeToPut)), null);

        Attendee loadedAttendee = new DynamoDBMapper(dynamoDB).load(Attendee.class, "paul.smith@email.com");
        assertEquals(attendeeToPut.getName(), loadedAttendee.getName());
    }
}