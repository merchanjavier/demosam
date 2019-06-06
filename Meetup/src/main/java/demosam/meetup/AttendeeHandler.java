package demosam.meetup;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import static java.net.HttpURLConnection.HTTP_CREATED;

public class AttendeeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDB;

    public AttendeeHandler() {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
    }

    public AttendeeHandler(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Attendee attendee = new Gson().fromJson(request.getBody(), Attendee.class);
        new DynamoDBMapper(dynamoDB).save(attendee);

        return new APIGatewayProxyResponseEvent().withBody(request.getBody()).withStatusCode(HTTP_CREATED);
    }
}
