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

let defaultMaxComments = 10;

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Returns a greeting function to a person
 * @param {string} name - the person to be greeted
 * @param {string} punctuation - the punctuation at the end of the statement
 * @return {string} - a message which greets a person with {name} upon being called 
 */
function greet(name, punctuation) {
  return `Hello ${name}${punctuation}`;
}

/**
 * Returns a happier version of an inputted string
 * @param {string} item - a body of text
 * @return {string} @param item with the addition of a laughing emoji
 */
function addHappiness(item) {
  return item + ' 😂';
}

/**
 * Updates the max number of comments which are showing
 * @return {void}
 */
async function updateMaxComments() {
  const maxComments = document.querySelector("#maxComments--number-input").value;
  defaultMaxComments = maxComments;
  await getData(maxComments);
}

/**
 * Deletes all comments which are stored in the datastore
 */
async function deleteComments() {
  try {
    const ret = await fetch('/delete-data', {
      method: "POST"
    })
    alert("A truly sad day, your comments were deleted");
    await getData();
  } catch (e) {
    alert("Something went wrong in deleting your comments");
  }
}

/**
 * Get the comments from the server and populate the comment DIV
 * @param {number?} maxComments - the max number of comments to display
 */
async function getData(maxComments = defaultMaxComments) {
  try {
    const ret = await fetch('/data?maxComments=' + maxComments);
    const comments = await ret.json();
    const commentDiv = document.querySelector('#comments');
    commentDiv.innerHTML = "";
    comments.forEach((comment, i) =>
      commentDiv.innerHTML += `<p>Comment ${i + 1} is ${comment}</p>`);
  } catch (e) {
    alert(`Hey!! There was an error: ${e?.message || e}`);
  }
}

/** Class representing a map of the countries with the highest happiness score */
class HappinessChart {
  /**
   * Create the happiness chart
   * @param {string} divID - The ID of the div which will contain the chart 
   */
  constructor(divID) {
    google.charts.load('current', {
      'packages': ['geochart'],
      'mapsApiKey': 'AIzaSyCQ2pBJaaQ0JIitodS6-sbHfie2Qosefxg'
    });
    google.charts.setOnLoadCallback(async () => {
      const happiestCountries = await this.getHappiestCountries();
      const data = this.buildDataTable(happiestCountries);
      this.drawRegionsMap(divID, data);
    });
  }

  /** 
   * Get the happiest countries from happiest_countries.json
   * @return {[
     [string, float]
    ]} an array of tuples with the first element of the tuple being the name
       and the second element being the happiness score
   */
  async getHappiestCountries() {
    const ret = await fetch('/static/data/happiest_countries.json');
    const countries = await ret.json();
    return countries.data.map((country) => [country.name, parseFloat(country.happinessScore)])
  }

  /**
   * Build the data table
   * @param {[
      [string, float]
    ]} happinessArray - an array of tuples with the first element of the tuple being the name
       and the second element being the happiness score
   * @return {DataTable} The created DataTable from the happiness array
   */
  buildDataTable(happinessArray) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Country');
    data.addColumn('number', 'Happiness Ranking');
    data.addRows(happinessArray);
    return data;
  }
  /**
   * Draws the chart within the targeted div
   * @param {string} divID - The ID of the div which will contain the map
   * @param {DataTable} data - The data for the happiness indexing 
   */
  drawRegionsMap(divID, data) {
    var options = {};
    var chart = new google.visualization.GeoChart(document.getElementById(divID));
    chart.draw(data, options);
  }
}

/**
 * Creates a map of a few places where I would want to travel and adds it to the page.
 */
function createMap() {
  const chiangMaiCoords = {
    lat: 18.7953, lng: 98.9620
  }
  const buenosAiresCoords = {
    lat: -34.6037, lng: -58.3816
  }
  const map = new google.maps.Map(
    document.getElementById('map'),
    { center: { lat: 37.422, lng: -122.084 }, zoom: 1 });
  const markerChiangMai = new google.maps.Marker({
    position: chiangMaiCoords,
    map: map,
    title: 'Chiang Mai',
    icon: 'static/icons/icons8-party-100.png'
  });
  const markerBuenosAires = new google.maps.Marker({
    position: buenosAiresCoords,
    map: map,
    title: 'Buenos Aires',
    icon: 'static/icons/icons8-party-100.png'
  });
  const markers = [markerBuenosAires, markerChiangMai];
  markers.forEach(marker => {
    const infowindow = new google.maps.InfoWindow({
      content: `<div>
  <p>Check it out! I would like to travel to ${marker.title}</p>
  <p>Let me know what you think?</p>
</div>`
    });
    marker.addListener('click', function() {
      infowindow.open(map, marker);
    });
  })
}

/**
 * Sets up the initial messages, comments, maps, and charts
 * called once the window loads
 */
function init() {
  greet('World', '!').split(' ').map(addHappiness).forEach(word => alert(word));
  getData();
  createMap();
  const happinessChart = new HappinessChart('happy-regions');
}

window.onload = init;
