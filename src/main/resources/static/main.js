let gameState = {
    playerList: [],
    lastOnStack: null,
    gameState: 'OPEN',
    activePlayer: null
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
    renderGameState() {
        let result = `<p><span class="badge rounded-pill bg-secondary">w trakcie rozgrywki</span>`
        if (this.gameState === 'OPEN') {
            result = `<p><span class="badge rounded-pill bg-success">otwarta, można dołączyć</span></p>`
        } else if (this.gameState === 'FINISHED') {
            result = `<p><span class="badge rounded-pill bg-success">zakończona, czeka na ponowne uruchomienie</span></p>`
        }
        result += this.renderPlayersList()
        return result;
    }

    renderPlayersList() {
        let result = '<ol class="list-group list-group-numbered">';
        this.playerList.forEach(player => {
            result += `<li class="list-group-item d-flex justify-content-between align-items-start"><div class="ms-2 me-auto"><div class="fw-bold">Gracz: ${player.name}</div>Status: ${player.state}</div></div>`
            result += ` <span class="badge bg-primary rounded-pill">karty: ${player.onHand.length}</span>`
        })
        return result;
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
            renderCardsOnHand()
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
            console.log("starting game...")
            getGameState();
            getPlayerState();
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
            console.log("starting game...")
            getGameState();
        });
}

const updateGameState = () => {
    const stateText = document.getElementById("game-state")
    stateText.innerHTML = gameState.renderGameState()
    if (gameState.gameState === "PLAYING") {
        populateLastCardOnStack(gameState.lastOnStack);
    }
}

const populateLastCardOnStack = (card) => {
    const domObj = document.getElementById("last-on-stack")
    const cardd = new Card(undefined, undefined, card)
    console.log(cardd)
    domObj.innerHTML = cardd.getCardHtml();
}

const renderCardsOnHand = () => {
    const onHand1 = document.getElementById("on-hand")
    let result = '';
    playerState.onHand = playerState.onHand.map(card => new Card(undefined, undefined, card));
    if (playerState.onHand !== undefined && playerState.onHand.length > 0) {
        playerState.onHand.forEach(card => {
            result += card.getOnHandCard();
        })
        onHand1.innerHTML = result;
    }
}

const cardAction = (color, value) => {
    const card = new Card(color, value);
    const indexToRemove = getCardIndexFromCardsOnHand(card)
    if (indexToRemove < 0) {
        console.log("card index not found")
        return;
    }
    if (!checkIfPutAsideIsPossible(card)) {
        console.log("nie mozesz polozyc tej karty")
        return;
    }
    putAside.push(card);
    playerState.onHand.splice(getCardIndexFromCardsOnHand(card), 1);
    renderCardsOnHand();
    renderPutAside();
}

const getCardIndexFromCardsOnHand = (card) => {
    for (let i = 0; i < playerState.onHand.length; i++) {
        if (playerState.onHand[i].equals(card)) {
            return i;
        }
    }
    return -1;
}


const renderPutAside = () => {
    const putAsideDom = document.getElementById("put-aside")
    const putAsideWrapperDom = document.getElementById("put-aside-wrapper")
    let result = '';
    if (putAside !== undefined && putAside.length > 0) {
        putAsideDom.style.display = 'block';
        putAsideWrapperDom.style.display = 'block';
        putAside.forEach(card => {
            const cardd = new Card(undefined, undefined, card)
            result += cardd.getPutAsideCard();
        })
        putAsideDom.innerHTML = result;
    } else {
        putAsideDom.style.display = 'none';
        putAsideWrapperDom.style.display = 'none';
    }
}

const move = () => {
    const moveDto = new Move(putAside);
    putAside = [];

    async function postData(url) {
        await fetch(url, {
            method: "POST",
            body: JSON.stringify(moveDto),
            headers: {
                'Content-Type': 'application/json',
                'uuid': getUserUuid()
            }
        });
    }

    postData('/api/game/move')
        .then(() => {
            console.log('user made move')
            updateGameState();
            getPlayerState();
            renderCardsOnHand();
            renderPutAside();
        })
        .catch(error => {
            console.error('There was an error!', error);
        });
}

const putCardsBackToHand = () => {
    console.log('putCardsBackToHand')
    playerState.onHand.push(...putAside);
    putAside = [];
    renderCardsOnHand();
    renderPutAside();
}

const checkIfPutAsideIsPossible = (card) => {
    if (putAside.length > 0) {
        const lastCard = putAside[putAside.length - 1];
        return cardResolver(lastCard, card, true);
    } else {
        const lastCard = gameState.lastOnStack;
        return cardResolver(lastCard, card, false);
    }
}

const cardResolver = (stack, newCard, next) => {
    if (next === true) {
        if (stack.value === newCard.value) {
            console.log('wartosc sie zgadza');
            return true;
        } else if (newCard.value === 'Queen') {
            console.log('dama na wszystko, wszystko na damę');
            return true;
        }
    } else {
        if (stack.color === newCard.color || stack.value === newCard.value) {
            console.log('kolor lub wartosc sie zgadza');
            return true;
        } else if (newCard.value === 'Queen') {
            console.log('dama na wszystko, wszystko na damę');
            return true;
        }
    }
    return false;
}

setInterval('refreshGame()', 1000)

function refreshGame() {
    getGameState();
}