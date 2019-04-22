package demosam.user;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.Map;

public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    DynamoDBMapper mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        mapper.save(new Gson().fromJson(request.getBody(), User.class));
        return new APIGatewayProxyResponseEvent().withBody(request.getBody()).withStatusCode(201);
    }

    public APIGatewayProxyResponseEvent getUser(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, String> pathParameters = request.getPathParameters();
        if (pathParameters == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400);
        }

        User user = mapper.load(User.class, pathParameters.get("userId"));

        if (user == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }

        return new APIGatewayProxyResponseEvent().withBody(new Gson().toJson(user)).withStatusCode(200);
    }
}
