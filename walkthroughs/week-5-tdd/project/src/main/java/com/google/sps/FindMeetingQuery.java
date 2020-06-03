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

import java.util.Collection;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<Event> restrainingEvents = filterEventsByAttendees(events, request.attendees.toArray());
    Collection<TimeRange> startTimes = getOrderedEndTimes(events);
    Collection<TimeRange> endTimes = getOrderedEndTimes(events);
    Collection<TimeRange> allOpenTimes = Collections.emptySet();
    for (Event event : events) {
        request.attendees.
    }
    openTimes.add(xx)
  }

  private Collection<TimeRange> getOpenTimes(Collection<TimeRange> startTimes,
                                             Collection<TimeRange> endTimes,
                                             long duration) {
    TimeRange openingTime = TimeRange.START_OF_DAY
    
    // TimeRange  TimeTimeRange.END_OF_DAY
  }

  private Collection<TimeRange> getOrderedEndTimes(Collection<Event> events) {
    Collection<TimeRange> startTimes = Collection.emptySet();
    for (Event event : events) {
      startTimes.add(event.when);
    }
    startTimes.sort(TimeRange.ORDER_BY_END);
    return startTimes;
  }

  private Collection<TimeRange> getOrderedStartTimes(Collection<Event> events) {
    Collection<TimeRange> startTimes = Collection.emptySet();
    for (Event event : events) {
      startTimes.add(event.when);
    }
    startTimes.sort(TimeRange.ORDER_BY_START);
    return startTimes;
  }

  private Collection<Event> filterEventsByAttendees(Collection<Event> event, String[] attendees) {
    Collection<Event> filteredEvents = Collection.emptySet();
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
