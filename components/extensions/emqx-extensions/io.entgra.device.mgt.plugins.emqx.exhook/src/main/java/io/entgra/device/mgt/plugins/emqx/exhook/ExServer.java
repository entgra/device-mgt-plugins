/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.emqx.exhook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.entgra.device.mgt.plugins.emqx.exhook.HandlerConstants.MIN_TOKEN_LENGTH;

public class ExServer {
    private static final Log logger = LogFactory.getLog(ExServer.class.getName());

    private static Map<String, String> accessTokenMap = new ConcurrentHashMap<>();
    private static Map<String, String> authorizedScopeMap = new ConcurrentHashMap<>();
    private Server server;
    private final ExServerUtilityService utilityService;

    public ExServer(ExServerUtilityService utilityService) {
        this.utilityService = utilityService;
    }

    public void start() throws IOException {
        /* The port on which the server should run */
        int port = 9000;

        server = ServerBuilder.forPort(port)
                .addService(new HookProviderImpl(utilityService))
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ExServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class HookProviderImpl extends HookProviderGrpc.HookProviderImplBase {

        public void DEBUG(String fn, Object req) {
            logger.debug(fn + ", request: " + req);
        }

        private final ExServerUtilityService utilityService;

        public HookProviderImpl(ExServerUtilityService utilityService) {
            this.utilityService = utilityService;
        }

        @Override
        public void onProviderLoaded(ProviderLoadedRequest request, StreamObserver<LoadedResponse> responseObserver) {
            DEBUG("onProviderLoaded", request);
            HookSpec[] specs = {
                    HookSpec.newBuilder().setName("client.connect").build(),
                    HookSpec.newBuilder().setName("client.connack").build(),
                    HookSpec.newBuilder().setName("client.connected").build(),
                    HookSpec.newBuilder().setName("client.disconnected").build(),
                    HookSpec.newBuilder().setName("client.authenticate").build(),
                    HookSpec.newBuilder().setName("client.check_acl").build(),
                    HookSpec.newBuilder().setName("client.subscribe").build(),
                    HookSpec.newBuilder().setName("client.unsubscribe").build(),

                    HookSpec.newBuilder().setName("session.created").build(),
                    HookSpec.newBuilder().setName("session.subscribed").build(),
                    HookSpec.newBuilder().setName("session.unsubscribed").build(),
                    HookSpec.newBuilder().setName("session.resumed").build(),
                    HookSpec.newBuilder().setName("session.discarded").build(),
                    HookSpec.newBuilder().setName("session.takeovered").build(),
                    HookSpec.newBuilder().setName("session.terminated").build(),

                    HookSpec.newBuilder().setName("message.publish").build(),
                    HookSpec.newBuilder().setName("message.delivered").build(),
                    HookSpec.newBuilder().setName("message.acked").build(),
                    HookSpec.newBuilder().setName("message.dropped").build()
            };
            LoadedResponse reply = LoadedResponse.newBuilder().addAllHooks(Arrays.asList(specs)).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onProviderUnloaded(ProviderUnloadedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onProviderUnloaded", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientConnect(ClientConnectRequest request, StreamObserver<EmptySuccess> responseObserver) {
            logger.info("onClientConnect -----------------------------");
            DEBUG("onClientConnect", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientConnack(ClientConnackRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onClientConnack", request);
            if (request.getResultCode().equals("success")) {
                handleDeviceStatusChange(request.getConninfo().getClientid(), EnrolmentInfo.Status.ACTIVE);
            }
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientConnected(ClientConnectedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onClientConnected", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientDisconnected(ClientDisconnectedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            logger.info("onClientDisconnected -----------------------------");
            DEBUG("onClientDisconnected", request);
            handleDeviceStatusChange(request.getClientinfo().getClientid(), EnrolmentInfo.Status.UNREACHABLE);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientAuthenticate(ClientAuthenticateRequest request, StreamObserver<ValuedResponse> responseObserver) {
            DEBUG("onClientAuthenticate", request);

            String username = request.getClientinfo().getUsername();
            String password = request.getClientinfo().getPassword();
            String clientId = request.getClientinfo().getClientid();

            try {
                if (StringUtils.isEmpty(clientId)) {
                    throw Status.INVALID_ARGUMENT
                            .withDescription("Client ID is missing in the request")
                            .asRuntimeException();
                }

                if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
                    throw Status.INVALID_ARGUMENT.
                            withDescription("Username and password are both empty.Either username, password or both must be provided").
                            asRuntimeException();
                }

                if (StringUtils.isNotEmpty(username) && StringUtils.isEmpty(password)) {
                    tryAuthenticateWithToken(username, responseObserver, clientId);
                    return;
                }

                //only opaque tokens with 36 characters or a JWT token of more than 36 characters is introspected.
                if (StringUtils.isNotEmpty(username)) {
                    if (StringUtils.isNotEmpty(password) && password.length() >= MIN_TOKEN_LENGTH) {
                        tryAuthenticateWithToken(password, responseObserver, clientId);
                        return;
                    }
                    tryAuthenticateWithToken((username + password), responseObserver, clientId);
                    return;
                }

                throw Status.UNAUTHENTICATED.withDescription("Invalid or expired token").asRuntimeException();
            } catch (StatusRuntimeException e) {
                respondGrpcError(responseObserver, e, "gRPC status error during client authentication");
            } catch (RuntimeException e) {
                respondGrpcError(responseObserver, e, "Unexpected error in onClientAuthenticate");
            }
        }

        /**
         * Attempts to authenticate a client using the provided OAuth token by
         * introspecting it against the configured Key Manager server.
         *
         * <p>This method sends a token introspection HTTP POST request to the
         * Key Manager's introspection endpoint. If the token is valid and active,
         * it updates the internal access and scope maps and responds with a
         * successful gRPC response. Otherwise, it responds with an appropriate
         * gRPC error status.</p>
         *
         * @param token            the OAuth token to authenticate
         * @param responseObserver the gRPC stream observer to send the response or error
         * @param clientId         the client identifier associated with the token
         * @throws IOException if an I/O error occurs while sending the introspection request
         */
        private void tryAuthenticateWithToken(String token, StreamObserver<ValuedResponse> responseObserver,
                                              String clientId) {
            try {
                KeyManagerConfigurations kmConfig = utilityService.getKeyManagerConfigurations();
                HttpPost tokenEndpoint = new HttpPost(kmConfig.getServerUrl() + HandlerConstants.INTROSPECT_ENDPOINT);
                tokenEndpoint.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
                tokenEndpoint.setHeader(HttpHeaders.AUTHORIZATION, HandlerConstants.BASIC +
                        Base64.getEncoder().encodeToString((kmConfig.getAdminUsername() + HandlerConstants.COLON +
                                kmConfig.getAdminPassword()).getBytes()));
                tokenEndpoint.setEntity(new StringEntity("token=" + token, ContentType.APPLICATION_FORM_URLENCODED));

                ProxyResponse tokenStatus = HandlerUtil.execute(tokenEndpoint);

                if (tokenStatus.getExecutorResponse().contains(HandlerConstants.EXECUTOR_EXCEPTION_PREFIX)) {
                    Status status = (tokenStatus.getCode() == HttpStatus.SC_UNAUTHORIZED)
                            ? Status.UNAUTHENTICATED
                            : Status.INTERNAL;
                    throw status.withDescription("Token introspection failed").asRuntimeException();
                }

                String tokenData = tokenStatus.getData();
                if (tokenData == null) {
                    throw Status.INTERNAL.withDescription("Token introspection response is empty").asRuntimeException();
                }

                JsonElement parsed = JsonParser.parseString(tokenData);
                if (!parsed.isJsonObject()) {
                    throw Status.INTERNAL.withDescription("Invalid token data format").asRuntimeException();
                }

                JsonObject tokenJson = parsed.getAsJsonObject();
                if (!tokenJson.get("active").getAsBoolean()) {
                    throw Status.UNAUTHENTICATED.withDescription("Token is inactive or expired").asRuntimeException();
                }

                // Token is valid
                accessTokenMap.put(clientId, token);
                authorizedScopeMap.put(token, tokenJson.get("scope").getAsString());

                ValuedResponse reply = ValuedResponse.newBuilder()
                        .setBoolResult(true)
                        .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                        .build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } catch (StatusRuntimeException e) {
                respondGrpcError(responseObserver, e, "gRPC status error");  // proper gRPC status error
            } catch (IOException e) {
                respondGrpcError(responseObserver, e, "IO error during authentication");
            }
        }

        @Override
        public void onClientCheckAcl(ClientCheckAclRequest request, StreamObserver<ValuedResponse> responseObserver) {
            DEBUG("onClientCheckAcl", request);
            /*
                carbon.super/deviceType/deviceId
                data/carbon.super/deviceType/deviceId
                data/carbonsuper/deviceType/deviceId
                republished/deviceType
             */
            try {
                //todo: check token validity
                String clientId = request.getClientinfo().getClientid();
                if (StringUtils.isEmpty(clientId)) {
                    throw Status.INVALID_ARGUMENT
                            .withDescription("Client ID is missing in the request")
                            .asRuntimeException();
                }
                String topic = request.getTopic();
                ClientCheckAclRequest.AclReqType aclType = request.getType();
                String accessToken = accessTokenMap.get(clientId);
                if (StringUtils.isEmpty(accessToken)) {
                    throw Status.PERMISSION_DENIED
                            .withDescription("Access token not found for clientId: " + clientId)
                            .asRuntimeException();
                }

                String authorizedScopeList = authorizedScopeMap.get(accessToken);
                if (StringUtils.isEmpty(authorizedScopeList)) {
                    throw Status.PERMISSION_DENIED
                            .withDescription("No authorized scopes found for token")
                            .asRuntimeException();
                }
                List<String> scopeList = Arrays.asList(authorizedScopeList.split(" "));
                String tempScope;
                switch (aclType) {
                    case PUBLISH:
                        String publishTopic = topic.replace("/", ":");
                        String[] topicParts = publishTopic.split(":");
                        if (topicParts.length >= 4 && "operation".equals(topicParts[3])) {
                            // publishing operation from iot server to emqx
                            tempScope = "perm:topic:pub:" + topicParts[0] + ":+:+:operation";
                        } else {
                            // publishing operation response from device to emqx
                            // publishing events from device to emqx
                            tempScope = "perm:topic:pub:" + publishTopic;
                        }
                        break;
                    case SUBSCRIBE:
                        // subscribing for events from iotserver to emqx
                        // subscribing for operation from device to emqx
                        // subscribing for operation response from iotserver to emqx
                        tempScope = "perm:topic:sub:" + (topic.endsWith("/#") ? topic.substring(0, topic.indexOf("/#")) : topic)
                                .replace("/", ":");
                        break;
                    default:
                        throw Status.INVALID_ARGUMENT
                                .withDescription("Unsupported ACL request type")
                                .asRuntimeException();
                }

                boolean isAuthorized = scopeList.stream().anyMatch(scope -> scope.startsWith(tempScope));
                if (!isAuthorized) {
                    logger.warn("topic=" + topic + ", requiredScope=" + tempScope);
                    throw Status.PERMISSION_DENIED.withDescription("User not authorized for requested topic").asRuntimeException();
                }

                // Authorized by default or passed scope check
                ValuedResponse reply = ValuedResponse.newBuilder()
                        .setBoolResult(true)
                        .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                        .build();

                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } catch (StatusRuntimeException e) {
                respondGrpcError(responseObserver, e, "ACL check failed: " + e.getStatus().getDescription());
            } catch (RuntimeException e) {
                respondGrpcError(responseObserver, e, "Unexpected error during ACL check");
            }
        }

        @Override
        public void onClientSubscribe(ClientSubscribeRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onClientSubscribe", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientUnsubscribe(ClientUnsubscribeRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onClientUnsubscribe", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionCreated(SessionCreatedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionCreated", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionSubscribed(SessionSubscribedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionSubscribed", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionUnsubscribed(SessionUnsubscribedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionUnsubscribed", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionResumed(SessionResumedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionResumed", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionDiscarded(SessionDiscardedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionDdiscarded", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionTakeovered(SessionTakeoveredRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionTakeovered", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onSessionTerminated(SessionTerminatedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onSessionTerminated", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onMessagePublish(MessagePublishRequest request, StreamObserver<ValuedResponse> responseObserver) {
            logger.info("onMessagePublish");
            ByteString bstr = ByteString.copyFromUtf8("hardcode payload by exhook-svr-java :)");

            Message nmsg = Message.newBuilder()
                    .setId(request.getMessage().getId())
                    .setNode(request.getMessage().getNode())
                    .setFrom(request.getMessage().getFrom())
                    .setTopic(request.getMessage().getTopic())
                    .setPayload(request.getMessage().getPayload()).build();


            ValuedResponse reply = ValuedResponse.newBuilder()
                    .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                    .setMessage(nmsg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

// case2: stop publish the 't/d' messages
//        @Override
//        public void onMessagePublish(MessagePublishRequest request, StreamObserver<ValuedResponse> responseObserver) {
//            DEBUG("onMessagePublish", request);
//
//            Message nmsg = request.getMessage();
//            if ("t/d".equals(nmsg.getTopic())) {
//                ByteString bstr = ByteString.copyFromUtf8("");
//                nmsg = Message.newBuilder()
//                              .setId     (request.getMessage().getId())
//                              .setNode   (request.getMessage().getNode())
//                              .setFrom   (request.getMessage().getFrom())
//                              .setTopic  (request.getMessage().getTopic())
//                              .setPayload(bstr)
//                              .putHeaders("allow_publish", "false").build();
//            }
//
//            ValuedResponse reply = ValuedResponse.newBuilder()
//                                                 .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
//                                                 .setMessage(nmsg).build();
//            responseObserver.onNext(reply);
//            responseObserver.onCompleted();
//        }

        @Override
        public void onMessageDelivered(MessageDeliveredRequest request, StreamObserver<EmptySuccess> responseObserver) {
            logger.info("onMessageDelivered");
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onMessageAcked(MessageAckedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onMessageAcked", request);
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onMessageDropped(MessageDroppedRequest request, StreamObserver<EmptySuccess> responseObserver) {
            logger.info("onMessageDropped ---------------------------------------------------------------");
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        }

        /**
         * Handles and responds to gRPC errors by logging the error and sending
         * an appropriate error status to the provided gRPC response observer.
         *
         * <p>If the throwable is already a {@link StatusRuntimeException}, it is
         * sent directly to the observer. Otherwise, an INTERNAL status with the
         * provided message and cause is created and sent.</p>
         *
         * @param observer the gRPC response observer to send the error to
         * @param e        the throwable representing the error
         * @param msg      a descriptive message to log and include in the gRPC error response
         */
        private void respondGrpcError(StreamObserver<?> observer, Throwable e, String msg) {
            if (e instanceof StatusRuntimeException) {
                logger.error(msg, e);
                observer.onError(e);
            } else {
                logger.error(msg, e);
                observer.onError(Status.INTERNAL.withDescription(msg).withCause(e).asRuntimeException());
            }
        }

        /**
         * Updates the status of a device for the given client ID.
         *
         * @param clientId the client identifier
         * @param status the new device status
         * @implNote Tenant info is currently hardcoded and should be dynamic in future.
         */
        private void handleDeviceStatusChange(String clientId, EnrolmentInfo.Status status) {
            String accessToken = accessTokenMap.get(clientId);
            if (StringUtils.isEmpty(accessToken)){
                logger.warn("No access token found for clientId: " + clientId);
                return;
            }
            String scopeString = authorizedScopeMap.get(accessToken);
            if (StringUtils.isEmpty(scopeString)) {
                logger.warn("No scope found for accessToken: " + accessToken);
                return;
            }
            String[] scopeArray = scopeString.split(" ");
            String deviceType = null;
            String deviceId = null;

            for (String scope : scopeArray) {
                if (scope.matches("^device:[^:]+:[^:]+$")) {
                    String[] scopeParts = scope.split(":");
                    deviceType = scopeParts[1];
                    deviceId = scopeParts[2];
                    break;
                }
            }

            if (!StringUtils.isEmpty(deviceType) && !StringUtils.isEmpty(deviceId)) {
                try {
                    PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    // TODO: fetch tenant dynamically instead of hardcoding
                    carbonContext.setTenantDomain("carbon.super");
                    carbonContext.setTenantId(-1234);
                    utilityService.changeDeviceStatus(new DeviceIdentifier(deviceId, deviceType), status);
                    logger.info(String.format("Device status changed successfully: [deviceType=%s, deviceId=%s, status=%s]", deviceType, deviceId, status));
                } catch (DeviceManagementException e) {
                    logger.error("Error while setting device status for deviceId: " + deviceId, e);
                }
            } else {
                logger.warn("Invalid or missing deviceType/deviceId for clientId: " + clientId);
            }
        }
    }

}
