/*
 * Copyright (c) 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ExServer {
    private static final Log logger = LogFactory.getLog(ExServer.class.getName());

    private static Map<String, String> accessTokenMap = new ConcurrentHashMap<>();
    private static Map<String, String> authorizedScopeMap = new ConcurrentHashMap<>();
    private Server server;

    public ExServer() {
    }

    public void start() throws IOException {
        /* The port on which the server should run */
        int port = 9000;

        server = ServerBuilder.forPort(port)
                .addService(new HookProviderImpl())
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

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final ExServer server = new ExServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class HookProviderImpl extends HookProviderGrpc.HookProviderImplBase {



        public void DEBUG(String fn, Object req) {
            logger.debug(fn + ", request: " + req);
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

        public static DeviceManagementProviderService getDeviceManagementService() {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                String msg = "DeviceImpl Management provider service has not initialized.";
                logger.error(msg);
//                throw new IllegalStateException(msg);
            }
            return deviceManagementProviderService;
        }
        @Override
        public void onClientConnack(ClientConnackRequest request, StreamObserver<EmptySuccess> responseObserver) {
            DEBUG("onClientConnack", request);
            if (request.getResultCode().equals("success")) {
                String accessToken = accessTokenMap.get(request.getConninfo().getClientid());
                String scopeString = authorizedScopeMap.get(accessToken);
                if (!StringUtils.isEmpty(scopeString)) {
                    String[] scopeArray = scopeString.split(" ");
                    String deviceType = null;
                    String deviceId = null;
                    for (String scope : scopeArray) {
                        if (scope.startsWith("device_")) {
                            String[] scopeParts = scope.split("_");
                            deviceType = scopeParts[1];
                            deviceId = scopeParts[2];
                            break;
                        }
                    }
                    if (!StringUtils.isEmpty(deviceType) && !StringUtils.isEmpty(deviceId)) {
                        try {
                            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain("carbon.super");
                            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(-1234);
                            DeviceManagementProviderService deviceManagementProviderService = getDeviceManagementService();
                            deviceManagementProviderService.changeDeviceStatus(new DeviceIdentifier(deviceId, deviceType), EnrolmentInfo.Status.ACTIVE);
                        } catch (DeviceManagementException e) {
                            logger.error("onClientConnack: Error while setting device status");
                        }
                    }
                }
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
            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onClientAuthenticate(ClientAuthenticateRequest request, StreamObserver<ValuedResponse> responseObserver) {
            DEBUG("onClientAuthenticate", request);

            if (!StringUtils.isEmpty(request.getClientinfo().getUsername()) &&
                    !StringUtils.isEmpty(request.getClientinfo().getPassword())) {

                DEBUG("on access token passes", request);
                try {
                    String accessToken = request.getClientinfo().getUsername() + "-" + request.getClientinfo().getPassword();
                    KeyManagerConfigurations keyManagerConfig = DeviceConfigurationManager.getInstance()
                            .getDeviceManagementConfig().getKeyManagerConfigurations();

                    HttpPost tokenEndpoint = new HttpPost(
                            keyManagerConfig.getServerUrl() + HandlerConstants.INTROSPECT_ENDPOINT);
                    tokenEndpoint.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
                    tokenEndpoint.setHeader(HttpHeaders.AUTHORIZATION, HandlerConstants.BASIC + Base64.getEncoder()
                            .encodeToString((keyManagerConfig.getAdminUsername() + HandlerConstants.COLON
                                    + keyManagerConfig.getAdminPassword()).getBytes()));
                    StringEntity tokenEPPayload = new StringEntity("token=" + accessToken,
                            ContentType.APPLICATION_FORM_URLENCODED);
                    tokenEndpoint.setEntity(tokenEPPayload);
                    ProxyResponse tokenStatus = HandlerUtil.execute(tokenEndpoint);

                    if (tokenStatus.getExecutorResponse().contains(HandlerConstants.EXECUTOR_EXCEPTION_PREFIX)) {
                        if (tokenStatus.getCode() == HttpStatus.SC_UNAUTHORIZED) {
                            // return with error
                            logger.error("Unauthorized");
                            responseObserver.onError(new Exception("unauthorized"));
                            return;
                        } else {
                            // return with error
                            logger.error("error occurred while checking access token");
                            responseObserver.onError(new Exception("error occurred while checking access token"));
                            return;
                        }
                    }

                    String tokenData = tokenStatus.getData();
                    if (tokenData == null) {
                        // return with error
                        logger.error("invalid token data is received");
                        responseObserver.onError(new Exception("invalid token data is received"));
                        return;
                    }
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jTokenResult = jsonParser.parse(tokenData);
                    if (jTokenResult.isJsonObject()) {
                        JsonObject jTokenResultAsJsonObject = jTokenResult.getAsJsonObject();
                        if (!jTokenResultAsJsonObject.get("active").getAsBoolean()) {
                            logger.error("access token is expired");
                            responseObserver.onError(new Exception("access token is expired"));
                            return;
                        }
                        // success
                        accessTokenMap.put(request.getClientinfo().getClientid(), accessToken);
                        authorizedScopeMap.put(accessToken, jTokenResultAsJsonObject.get("scope").getAsString());
                        logger.info("authenticated");
                        ValuedResponse reply = ValuedResponse.newBuilder()
                                .setBoolResult(true)
                                .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                                .build();
                        responseObserver.onNext(reply);
                        responseObserver.onCompleted();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            } else {
//                ValuedResponse reply = ValuedResponse.newBuilder()
//                        .setBoolResult(true)
//                        .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
//                        .build();
//                responseObserver.onNext(reply);
//                responseObserver.onCompleted();
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
            if (!StringUtils.isEmpty(request.getClientinfo().getUsername()) &&
                    StringUtils.isEmpty(request.getClientinfo().getPassword())) {
                //todo: check token validity
                String accessToken = accessTokenMap.get(request.getClientinfo().getClientid());
                if (StringUtils.isEmpty(accessToken) || !accessToken.startsWith(request.getClientinfo().getUsername())) {
                    logger.info("Valid access token not found");
                    responseObserver.onError(new Exception("not authorized"));
                    return;
                }

                String authorizedScopeList = authorizedScopeMap.get(accessToken);
                boolean isFound = false;
                if (!StringUtils.isEmpty(authorizedScopeList)) {
                    String[] scopeArray = authorizedScopeList.split(" ");
                    List<String> scopeList = Arrays.asList(scopeArray);


                    String tempScope = null;
                    String requestTopic = request.getTopic();

                    if (request.getType().equals(ClientCheckAclRequest.AclReqType.PUBLISH)) {
                        requestTopic = requestTopic.replace("/", ":");

                        String[] requestTopicParts = requestTopic.split(":");

                        if (requestTopicParts.length >= 4 && "operation".equals(requestTopicParts[3])) {
                            // publishing operation from iot server to emqx
                            tempScope = "perm:topic:pub:" + requestTopicParts[0] + ":+:+:operation";
                        } else {
                            // publishing operation response from device to emqx
                            // publishing events from device to emqx
                            tempScope = "perm:topic:pub:" + requestTopic;
                        }

                        for (String scope : scopeList) {
                            if (scope.startsWith(tempScope)) {
                                isFound = true;
                                break;
                            }
                        }
                    }

                    if (request.getType().equals(ClientCheckAclRequest.AclReqType.SUBSCRIBE)) {
                        if (requestTopic.endsWith("/#")) {
                            requestTopic = requestTopic.substring(0, requestTopic.indexOf("/#"));
                        }

                        requestTopic = requestTopic.replace("/", ":");
                        // subscribing for events from iotserver to emqx
                        // subscribing for operation from device to emqx
                        // subscribing for operation response from iotserver to emqx
                        tempScope = "perm:topic:sub:" + requestTopic;

                        for (String scope : scopeList) {
                            if (scope.startsWith(tempScope)) {
                                isFound = true;
                                break;
                            }
                        }
                    }
                }

                if (isFound) {
                    ValuedResponse reply = ValuedResponse.newBuilder()
                            .setBoolResult(true)
                            .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                            .build();

                    responseObserver.onNext(reply);
                    responseObserver.onCompleted();
                } else {
                    logger.error("not authorized");
                    responseObserver.onError(new Exception("not authorized"));
                }

            } else {
                //default
                ValuedResponse reply = ValuedResponse.newBuilder()
                        .setBoolResult(true)
                        .setType(ValuedResponse.ResponsedType.STOP_AND_RETURN)
                        .build();

                responseObserver.onNext(reply);
                responseObserver.onCompleted();
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

            String accessToken = accessTokenMap.get(request.getClientinfo().getClientid());
            if (!StringUtils.isEmpty(accessToken)) {
                String scopeString = authorizedScopeMap.get(accessToken);
                String[] scopeArray = scopeString.split(" ");
                String deviceType = null;
                String deviceId = null;
                for (String scope : scopeArray) {
                    if (scope.startsWith("device:")) {
                        String[] scopeParts = scope.split(":");
                        deviceType = scopeParts[1];
                        deviceId = scopeParts[2];
                        break;
                    }
                }
                if (!StringUtils.isEmpty(deviceType) && !StringUtils.isEmpty(deviceId)) {
                    try {
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain("carbon.super");
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(-1234);
                        DeviceManagementProviderService deviceManagementProviderService = getDeviceManagementService();;
                        deviceManagementProviderService.changeDeviceStatus(new DeviceIdentifier(deviceId, deviceType), EnrolmentInfo.Status.UNREACHABLE);
                    } catch (DeviceManagementException e) {
                        logger.error("onSessionTerminated: Error while setting device status");
                    }
                }
            }

            EmptySuccess reply = EmptySuccess.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void onMessagePublish(MessagePublishRequest request, StreamObserver<ValuedResponse> responseObserver) {
            logger.info("onMessagePublish");
            ByteString bstr = ByteString.copyFromUtf8("hardcode payload by exhook-svr-java :)");

            Message nmsg = Message.newBuilder()
                                  .setId     (request.getMessage().getId())
                                  .setNode   (request.getMessage().getNode())
                                  .setFrom   (request.getMessage().getFrom())
                                  .setTopic  (request.getMessage().getTopic())
                                  .setPayload(((GeneratedMessageV3) request).toByteString()).build();


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


    }


}
