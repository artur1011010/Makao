let gameState = {
    playerList: [],
    lastOnStack: null,
    gameState: 'OPEN',
    active: null
}

let playerState = {
    name: '',
    onHand: [],
    state: 'IDLE',
    movementsBlocked: 0,
    uuid: ''
}

let putAside = [];

class GameState {
    constructor(obj) {
        obj && Object.assign(this, obj);
    }
}

class PlayerState {
    constructor(obj) {
        obj && Object.assign(this, obj);
    }
}

class Card {
    constructor(color, value, obj) {
        if (obj !== undefined) {
            obj && Object.assign(this, obj);
        } else {
            this.color = color;
            this.value = value;
        }
    }

    getCardHtml() {
        return `<div class="card ${this.color + this.value}" ${this.getImage(this.color, this.value)}></div>`;
    }

    getOnHandCard() {
        return `<div class="card action-card ${this.color + this.value}" ${this.getImage(this.color, this.value)} 
            onClick='cardAction("${this.color}" , "${this.value}")' data-bs-toggle="tooltip" data-bs-placement="top" title="połóż kartę"></div>`;
    }

    getPutAsideCard() {
        return `<div class="card ${this.color + this.value}" ${this.getImage(this.color, this.value)} data-bs-toggle="tooltip" data-bs-placement="top" title="możesz cofnąc wybór przyciskiem"></div>`;
    }

    getImage(color, value) {
        return `style='background-image: url("./img/cards/${color.toLowerCase()}-${value.toLowerCase()}.png");'`;
    }

    equals(object2) {
        return this.color === object2.color && this.value === object2.value;
    }
}

class Move {
    constructor(putAside) {
        this.putAside = putAside;
    }
}

const createPlayer = () => {
    const name = document.getElementById("player-name").value
    if (!name || name.length < 1) {
        alert("imie nie moze byc puste")
        return;
    }

    async function getData(url) {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
        });
        return response.json();
    }

    getData('/api/player?name=' + name)
        .then((data) => {
            console.log("uzytkownik stworzony i dodany do gry z uuid: " + data.uuid)
            setUserUuid(data.uuid);
        });
}

const setUserUuid = (uuid) => {
    localStorage.setItem("uuid", uuid);
}

const getUserUuid = () => {
    return localStorage.getItem("uuid");
}

const getPlayerState = () => {
    async function getData(url) {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                'uuid': getUserUuid()
            },
        });
        return response.json();
    }

    getData('/api/player/state')
        .then(data => {
            playerState = new GameState(data)
            populateCardsOnHand()
        });
}

const getGameState = () => {
    async function getData(url) {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json'
            },
        });
        return response.json();
    }

    getData('/api/game/state')
        .then(data => {
            console.log(data)
            gameState = new GameState(data)
            // updateGameState(new GameState(data));
            updateGameState();
        });
}

const startGame = () => {
    async function getData(url) {
        await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
        });
    }

    getData('/api/game/start')
        .then(() => {
            console.log("rozpoczynanie gry...")
            getGameState();
        });
}

const restartGame = () => {
    async function getData(url) {
        await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
        });
    }

    getData('/api/game/restart')
        .then(() => {
            console.log("rozpoczynanie gry od nowa...")
            getGameState();
        });
}

const updateGameState = () => {
    const stateText = document.getElementById("game-state")
    stateText.innerHTML = gameState.gameState;
    if (gameState.gameState === "PLAYING") {
        populateLastCardOnStack(gameState.lastOnStack);
    }
    getPlayerState();
}


const populateLastCardOnStack = (card) => {
    const domObj = document.getElementById("last-on-stack")
    const cardd = new Card(undefined, undefined, card)
    console.log(cardd)
    domObj.innerHTML = cardd.getCardHtml();
}

const populateCardsOnHand = () => {
    const onHand1 = document.getElementById("on-hand")
    const cards = playerState.onHand;
    let result = '';
    if (cards !== undefined && cards.length > 0) {
        cards.forEach(card => {
            const cardd = new Card(undefined, undefined, card)
            result += cardd.getOnHandCard();
        })
        onHand1.innerHTML = result;
    }
}

const cardAction = (color, value) => {
    putAside.push(new Card(color, value));
    renderPutAside();
}

const renderPutAside = () => {
    const putAsideDom = document.getElementById("put-aside")
    const putAsideHeaderDom = document.getElementById("put-aside-header")
    let result = '';
    if (putAside !== undefined && putAside.length > 0) {
        putAsideDom.style.display = 'block';
        putAsideHeaderDom.style.display = 'block';
        putAside.forEach(card => {
            const cardd = new Card(undefined, undefined, card)
            result += cardd.getPutAsideCard();
        })
        putAsideDom.innerHTML = result;
    } else {
        putAsideDom.style.display = 'none';
        putAsideHeaderDom.style.display = 'none';
    }
}

const checkIfPutAsideIsPossible = (card) => {

}

setInterval('refreshGame()', 1000)

function refreshGame() {
    // console.log(Date.now())
}