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

function composeWorld(f) {
    return f('world');
}

function greet(name) {
    return (punctuation) => `Hello ${name}${punctuation}`;
}

function addHappiness(item) {
    return item + ' 😂';
}


async function updateMaxComments() {
    const maxComments = document.querySelector("#maxComments--number-input").value;
    await getData(maxComments);
}

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

async function getData(maxComments=10) {
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

function init() {
    composeWorld(greet)('!').split(' ').map(addHappiness).forEach(word => alert(word));
    getData();
}

window.onload = init