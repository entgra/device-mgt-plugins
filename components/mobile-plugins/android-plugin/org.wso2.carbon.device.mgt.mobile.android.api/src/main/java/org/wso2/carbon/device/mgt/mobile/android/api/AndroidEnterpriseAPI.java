/*
 *   Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
package org.wso2.carbon.device.mgt.mobile.android.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.ResponseHeader;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationPolicyDTO;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStoreCluster;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStorePage;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStorePageLinks;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseManagedConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "Android Enterprise Service"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/android/v1.0/enterprise"),
                        })
                }
        ),
        tags = {
                @Tag(name = "android,device_management", description = "Android Device Management Service")
        }
)

@Api(value = "Android Enterprise Service")
@Path("/enterprise")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Scopes(
        scopes = {
                @Scope(
                        name = "Manage Enterprise User",
                        description = "Managing Enterprise users in the system",
                        key = "perm:enterprise:modify",
                        permissions = {"/device-mgt/enterprise/user/modify"}
                ),
                @Scope(
                        name = "View Enterprise User",
                        description = "View enterprise users in the system",
                        key = "perm:enterprise:view",
                        permissions = {"/device-mgt/enterprise/user/view"}
                )
        }
)
public interface AndroidEnterpriseAPI {

    @POST
    @Path("/user")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add User",
            notes = "Add a new user to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new user",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new user.")
            })
    Response addUser(@ApiParam(
            name = "user",
            value = "Enterprise user and device data.") EnterpriseUser enterpriseUser);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @POST
    @Path("/available-app")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add User",
            notes = "Add a new user to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new user",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new user.")
            })
    Response updateUser(@ApiParam(
            name = "device",
            value = "Enterprise user and device data.") EnterpriseInstallPolicy device);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @GET
    @Path("/store-url")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting Details of a Device Type",
            notes = "Get the details of a device by searching via the device type and the tenant domain.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created. \n Successfully added new user",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while adding new user.")
    })
    Response getStoreUrl(
            @ApiParam(
            name = "approveApps",
            value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("approveApps") boolean approveApps,

            @ApiParam(
            name = "searchEnabled",
            value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("searchEnabled") boolean searchEnabled,

            @ApiParam(
            name = "isPrivateAppsEnabled",
            value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("isPrivateAppsEnabled") boolean isPrivateAppsEnabled,

            @ApiParam(
            name = "isWebAppEnabled",
            value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("isWebAppEnabled") boolean isWebAppEnabled,

            @ApiParam(
            name = "isOrganizeAppPageVisible",
            value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("isOrganizeAppPageVisible") boolean isOrganizeAppPageVisible,

            @ApiParam(
                    name = "isManagedConfigEnabled",
                    value = "Boolean flag indicating whether to permanently delete the device.")
            @QueryParam("isManagedConfigEnabled") boolean isManagedConfigEnabled,

            @ApiParam(name = "host",
            value = "Boolean flag indicating whether to permanently delete the device.",
            required = true) @QueryParam("host") String host);

    @GET
    @Path("/products/sync")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting Details of a Device Type",
            notes = "Get the details of a device by searching via the device type and the tenant domain.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created. \n Successfully added new user",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while adding new user.")
    })
    Response syncApps();

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @POST
    @Path("/store-layout/page")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add New Page",
            notes = "Add a new page to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new page",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new page.")
            })
    Response addPage(@ApiParam(
            name = "page",
            value = "Enterprise page.") EnterpriseStorePage page);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @PUT
    @Path("/store-layout/page")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update New Page",
            notes = "Update a new page to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully update page",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating new page.")
            })
    Response updatePage(@ApiParam(
            name = "page",
            value = "Enterprise page.")
                             EnterpriseStorePage page);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @DELETE
    @Path("/store-layout/page/{id}")
    @Consumes(MediaType.WILDCARD)
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "DELETE",
            value = "Deleting Details of a Device Type",
            notes = "Deleting the details of a device by searching via the device type and the tenant domain.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully Delete new user",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while Deleting new user.")
    })
    Response deletePage(@ApiParam(
            name = "id",
            value = "The unique page id")
                        @PathParam("id") String id);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @GET
    @Path("/store-layout/page")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Deleting Details of a Device Type",
            notes = "Deleting the details of a device by searching via the device type and the tenant domain.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully get new user",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while getting new user.")
    })
    Response getPages();

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @PUT
    @Path("/store-layout/home-page/{id}")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update Home Page",
            notes = "Update the home page",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully updated home page",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating home page.")
            })
    Response setHome(@ApiParam(
            name = "id",
            value = "The unique page id")
                     @PathParam("id") String id);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @GET
    @Path("/store-layout/home-page")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get Home Page",
            notes = "Get a home page",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Created. \n Successfully update home page",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating home page.")
            })
    Response getHome();

    //######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @POST
    @Path("/store-layout/cluster")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add New Cluster",
            notes = "Add a new cluster to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new cluster",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new cluster.")
            })
    Response addCluster(@ApiParam(
            name = "storeCluster",
            value = "Enterprise cluster.") EnterpriseStoreCluster storeCluster);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @PUT
    @Path("/store-layout/cluster")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update cluster",
            notes = "Update cluster.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully updated cluster",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating cluster.")
            })
    Response updatePage(@ApiParam(
            name = "storeCluster",
            value = "Enterprise page.")
                                EnterpriseStoreCluster storeCluster);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @DELETE
    @Path("/store-layout/cluster/{clusterId}/page/{pageId}")
    @Consumes(MediaType.WILDCARD)
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "DELETE",
            value = "Deleting Details of a cluster",
            notes = "Deleting the details of a cluster.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully Deleted cluster",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while Deleting cluster.")
    })
    Response deleteCluster(@ApiParam(
            name = "clusterId",
            value = "The unique cluster id")
                        @PathParam("clusterId") String clusterId,
                        @ApiParam(
                                name = "pageId",
                                value = "The unique page pageId")
                        @PathParam("pageId") String pageId);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################


    @GET
    @Path("/store-layout/page/{id}/clusters")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Deleting Details of a cluster",
            notes = "Deleting the details of a cluster.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully fetched cluster",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while getting cluster.")
    })
    Response getClustersInPage(
            @ApiParam(
            name = "pageId",
            value = "The unique page pageId")
                               @PathParam("id") String pageId);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @PUT
    @Path("/store-layout/page-link")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add New Cluster",
            notes = "Add a new cluster to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new cluster",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new cluster.")
            })
    Response updateLinks(@ApiParam(
            name = "links",
            value = "Enterprise page links.") EnterpriseStorePageLinks links);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @GET
    @Path("/managed-configs/package/{packageName}")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting managed configs",
            notes = "Getting managed configs.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:view")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully fetched managed configs",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while getting managed configs.")
    })
    Response getConfig(@ApiParam(
                    name = "packageName",
                    value = "The package name")
            @PathParam("packageName") String packageName);


//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @POST
    @Path("/managed-configs")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add New config",
            notes = "Add a new config to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Created. \n Successfully added new config",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while adding new config.")
            })

    Response addManagedConfigs(@ApiParam(
            name = "managedConfig",
            value = "Enterprise managed conf.") AndroidEnterpriseManagedConfig managedConfig);

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @PUT
    @Path("/managed-configs")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update config",
            notes = "Update config to enterprise system.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Created. \n Successfully updated new config",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating new config.")
            })

    Response updateManagedConfigs(@ApiParam(
            name = "managedConfig",
            value = "Enterprise managed conf.") AndroidEnterpriseManagedConfig managedConfig);


//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @DELETE
    @Path("/managed-configs/mcm/{mcmId}")
    @Consumes(MediaType.WILDCARD)
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "DELETE",
            value = "Deleting Details of a config",
            notes = "Deleting the details of a config.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully deleted config",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while deleting config.")
    })
    Response deleteManagedConfigs(@ApiParam(
            name = "mcmId",
            value = "The mcm Id")
                                  @PathParam("mcmId") String packageName);


//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @POST
    @Path("/change-app")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Update apps in device",
            notes = "Update apps in device.",
            tags = "Android Enterprise Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Created. \n Successfully updated new config",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the added policy."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")
                            }),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error."),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The format of the requested entity was not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while updating new config.")
            })
    Response updateUser(@ApiParam(
            name = "applicationPolicyDTO",
            value = "Enterprise managed conf.") ApplicationPolicyDTO applicationPolicyDTO);


//######################################################################################################################
//######################################################################################################################
//######################################################################################################################

    @GET
    @Path("/wipe-device")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting managed configs",
            notes = "Getting managed configs.",
            tags = "Device Type Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidConstants.SCOPE, value = "perm:enterprise:modify")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created. \n Successfully fetched managed configs",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The URL of the added policy."),
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests.")
                    }),
            @ApiResponse(
                    code = 303,
                    message = "See Other. \n The source can be retrieved from the URL specified in the location header.",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Location",
                                    description = "The Source URL of the document.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while getting managed configs.")
    })
    Response wipeEnterprise();

}
