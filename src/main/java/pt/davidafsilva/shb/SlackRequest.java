package pt.davidafsilva.shb;

/*
 * #%L
 * slack-hello-world
 * %%
 * Copyright (C) 2015 David Silva
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

import io.vertx.core.json.Json;

/**
 * The slack request data
 *
 * @author David Silva
 */
public final class SlackRequest {

  // JSON properties
  // ---------------

  // the API token
  private String token;
  // the timestamp of the request
  private Instant timestamp;

  // team data
  @JsonProperty("team_id")
  private String teamIdentifier;
  @JsonProperty("team_domain")
  private String teamDomain;

  // channel data
  @JsonProperty("channel_id")
  private String channelId;
  @JsonProperty("channel_name")
  private String channelName;

  // user data
  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("user_name")
  private String userName;

  // actual request data
  @JsonProperty("trigger_word")
  private String trigger;
  private String text;

  /**
   * Returns the API token
   *
   * @return the API token
   */
  public String getToken() {
    return token;
  }

  /**
   * Returns the request timestamp
   *
   * @return the request timestamp
   */
  public Instant getTimestamp() {
    return timestamp;
  }

  /**
   * Returns the team identifier
   *
   * @return the team identifier
   */
  public String getTeamIdentifier() {
    return teamIdentifier;
  }

  /**
   * Returns the team domain name
   *
   * @return the domain name
   */
  public String getTeamDomain() {
    return teamDomain;
  }

  /**
   * Returns the channel identifier from which the message was sent
   *
   * @return the channel id
   */
  public String getChannelId() {
    return channelId;
  }

  /**
   * Returns the channel name from which the message was sent
   *
   * @return the channel name
   */
  public String getChannelName() {
    return channelName;
  }

  /**
   * The user identifier that triggered the request
   *
   * @return the user identifier
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Returns the user name that triggered the request
   *
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Returns the configured trigger that originated this request
   *
   * @return the trigger command
   */
  public String getTrigger() {
    return trigger;
  }

  /**
   * Returns the full text message
   *
   * @return the full text message
   */
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
