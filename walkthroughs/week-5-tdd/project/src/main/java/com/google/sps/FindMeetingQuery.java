// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;

/**
 * Class which finds a range of times when all required attendees can meet
 * If there are no available times when an all optional attendees can meet,
 * then only required attendees are considered in finding available times 
 */
public final class FindMeetingQuery {

  /**
   * Find available time ranges to meet
   * @param events All events which are occuring 
   * @param request A requested meeting with certain required and optional attendees and a duration
   * @return all time ranges when the required attendess, and possibly the optional attendees, can meet
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> allAttendees = new ArrayList<String>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    Collection<TimeRange> openTimes = queryWithAttendees(events, request, allAttendees);
    if (request.getAttendees().size() == 0 || openTimes.size() != 0)
      return openTimes;
    return queryWithAttendees(events, request, request.getAttendees());
  }

  /**
   * Find available time ranges to meet for a specific collection of attendees
   * @param events All events which are occuring 
   * @param request A requested meeting with certain required and optional attendees and a duration
   * @param attendees A collection of attendees which will be considered in finding available time ranges
   * @return all time ranges when the {@code attendess} can meet
   */
  private Collection<TimeRange> queryWithAttendees(Collection<Event> events,
                                                  MeetingRequest request,
                                                  Collection<String> attendees) {
    Collection<Event> restrainingEvents = filterEventsByAttendees(events, attendees);
    Collection<TimeRange> eventTimeRanges = getTimeRangesFromEvents(restrainingEvents);
    Collection<TimeRange> occupiedTimes = getNonOverlappingTimes(eventTimeRanges);
    return getAvailableTimes(occupiedTimes, request.getDuration());
  }

  /** 
   * Checks if time overlaps with addedTimes. If it does, it will change addedTimes so that
   * addedTimes contains time in it without having any overlap
   * @param addedTimes All the {@code TimeRange} which do not overlap
   * @param time A time to be added to {@code addedTimes}. {@code addedTimes} is changed to include time
   * without overlapping any {@code TimeRange}
   */
  private void mergeToAddedTimes(Collection<TimeRange> addedTimes, TimeRange time) {
    Collection<TimeRange> overlappingTimes = new ArrayList<TimeRange>();
    // find all overlapping times
    for (TimeRange includedTime : addedTimes) {
      if (includedTime.overlaps(time)) {
        overlappingTimes.add(includedTime);
      }
    }

    int minStart = time.start();
    int maxEnd = time.end();

    for (TimeRange t : overlappingTimes) {
      if (t.start() < minStart) minStart = t.start();
      if (t.end() > maxEnd) maxEnd = t.end();
      addedTimes.remove(t);
    }
    addedTimes.add(TimeRange.fromStartEnd(minStart, maxEnd, false));
  }

  /**
   * From a potentially overlapping collection of times, this method finds all the time ranges covered
   * @param times A potentially overlapping collection of {@code TimeRange}
   * @return A collection of {@code TimeRange} which covers the same times as {@code times}, but has no
   * overlapping {@code TimeRange} 
   */
  private Collection<TimeRange> getNonOverlappingTimes(Collection<TimeRange> times) {
    Collection<TimeRange> timeRanges = new ArrayList<TimeRange>();
    for (TimeRange t : times)
      mergeToAddedTimes(timeRanges, t);
    return timeRanges;
  }

  /**
   * Get all of the collection of times which correlate to events
   * @param events A collection of all the events to get time ranges from
   * @return A collection of times which correlate to events
   */
  private Collection<TimeRange> getTimeRangesFromEvents(Collection<Event> events) {
    Collection<TimeRange> timeRanges = new ArrayList<TimeRange>();
    for (Event event : events) {
      timeRanges.add(event.getWhen());
    }
    return timeRanges;
  }

  /**
   * Get a collection of times which are longer than or equal to in length to the {@code duration}
   * and which do not overlap with the {@code occupiedTImes}
   * @param occupiedTimes A collection of times which cannot overlap with the result
   * @param duration The minimum length of time which can be returned in the collection
   * @return A collection of times which are longer than or equal to in length to the {@code duration}
   * and which do not overlap with the {@code occupiedTImes}
   */
  private Collection<TimeRange> getAvailableTimes(Collection<TimeRange> occupiedTimes,
                                             long duration) {
    if (occupiedTimes.size() == 0) {
      if (TimeRange.WHOLE_DAY.duration() >= duration)
        return Arrays.asList(TimeRange.WHOLE_DAY);
      return Arrays.asList();
    }

    // order the times by the start
    Collections.sort((ArrayList<TimeRange>) occupiedTimes, TimeRange.ORDER_BY_START);

    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();
    int availableStartTime = TimeRange.START_OF_DAY;
    
    // find each moment of the day which is not occupied
    for (TimeRange occupiedTime : occupiedTimes) {
      TimeRange openTime = TimeRange.fromStartEnd(availableStartTime, occupiedTime.start(), false);
      if (openTime.duration() >= duration)
        availableTimes.add(openTime);
      availableStartTime = occupiedTime.end();
    }
    TimeRange lastTimeOfDay = TimeRange.fromStartEnd(availableStartTime, TimeRange.END_OF_DAY, true);
    if (lastTimeOfDay.duration() >= duration)
      availableTimes.add(lastTimeOfDay);
    return availableTimes;
  }

  /** 
   * Get only the events which are attended by {@code attendees}
   * @param events A collection of events which then get filtered by attendees
   * @param attendees A collection of attendees which must be in. One attendee must be in every
   * returning event
   * @return A collection of events filtered by the {@code attendees}
   */
  private Collection<Event> filterEventsByAttendees(Collection<Event> events, Collection<String> attendees) {
    Collection<Event> filteredEvents = new ArrayList<Event>();
    for (Event event : events) {
      boolean containsAttendee = false;
      for (String attendee : attendees) {
        if (event.getAttendees().contains(attendee)) {
          containsAttendee = true;
          break;
        }
      }
      if (containsAttendee)
        filteredEvents.add(event);
    }
    return filteredEvents;
  }
}
