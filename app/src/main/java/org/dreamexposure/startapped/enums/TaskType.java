package org.dreamexposure.startapped.enums;

/**
 * @author NovaFox161
 * Date Created: 12/24/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public enum TaskType {
    ACCOUNT_GET_BLOG, ACCOUNT_GET_SELF, ACCOUNT_UPDATE,

    AUTH_LOGIN, AUTH_LOGOUT, AUTH_REGISTER, AUTH_TOKEN_REAUTH,

    BLOG_CREATE, BLOG_GET_SELF_EDIT, BLOG_GET_SELF_ALL, BLOG_UPDATE_SELF, BLOG_GET_VIEW,

    DOWNLOAD_IMAGE,

    FOLLOW_FOLLOW_BLOG, FOLLOW_UNFOLLOW_BLOG, FOLLOW_GET_FOLLOWERS, FOLLOW_GET_FOLLOWING,

    POST_CREATE,

    POST_GET_FOR_BLOG, POST_GET_FOR_HUB, POST_GET_FOR_SEARCH, POST_GET_SINGLE
}
