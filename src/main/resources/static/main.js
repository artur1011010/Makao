//Classes
class GameState {
    constructor(obj, playerList, lastOnStack, gameState, activePlayerUuid) {
        if (obj !== undefined) {
            obj && Object.assign(this, obj);
        } else {
            this.playerList = playerList;
            this.lastOnStack = lastOnStack;
            this.gameState = gameState;
            this.activePlayerUuid = activePlayerUuid;
        }
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
            result += `<li class="list-group-item d-flex justify-content-between align-items-start">
                        <div class="ms-2 me-auto">
                        <div class="fw-bold">Gracz: ${player.name}</div>
                        Status: ${this.getPolishDescFromState(player.state)}</div>
                        </div> 
                        <span class="badge bg-primary rounded-pill">karty: ${player.onHand.length === 1 ? "Makao! " + player.onHand.length : player.onHand.length}</span>`
        })
        return result;
    }

    getPolishDescFromState(state) {
        let state1;
        switch (state) {
            case "ACTIVE":
                state1 = "wykonuje ruch"
                break;
            case "BLOCKED":
                state1 = "zablokowany w następnej kolejce"
                break;
            case "WAITING":
                state1 = "czeka na swoją kolej"
                break;
            case "FINISHED":
                state1 = "wygrał! - zakonczył gre"
                break;
            case "IDLE":
                state1 = "bezczynny - nie uczestniczy w grze"
                break;
        }
        return state1;
    }

    equals(object) {
        return this.activePlayer === object.activePlayer;
    }
}

class PlayerState {
    constructor(obj) {
        obj && Object.assign(this, obj);
    }

    equals(object) {
        return this.state === object.state;
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

    getDisabledCardOnHand() {
        return `<div class="card action-disabled ${this.color + this.value}" ${this.getImage(this.color, this.value)} 
             data-bs-toggle="tooltip" data-bs-placement="top" title="nie możesz użyć tej karty w tym ruchu"></div>`;
    }

    getPutAsideCard() {
        return `<div class="card ${this.color + this.value}" ${this.getImage(this.color, this.value)} data-bs-toggle="tooltip" data-bs-placement="top" title="możesz cofnąc wybór przyciskiem"></div>`;
    }

    getImage(color, value) {
        return `style='background-image: url("./img/cards/${color.toLowerCase()}-${value.toLowerCase()}.png");'`;
    }

    isRequested() {
        if (playerState.requestedCardsInNextMove !== undefined && playerState.requestedCardsInNextMove !== null && playerState.requestedCardsInNextMove.length > 0) {
            const array = playerState.requestedCardsInNextMove;
            for (const card of array) {
                if (card.color === this.color && card.value === this.value) {
                    return true;
                }
            }
        }
        return false;
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

//Global variables

let gameState = new GameState(undefined, [], null, 'OPEN', null);

let playerState = {
    name: '',
    state: 'IDLE',
    movementsBlocked: 0,
    onHand: [],
    requestedCardsInNextMove: [],
    uuid: ''
}

let putAside = [];

//FUNCTIONS

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
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
        });
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
            playerState = new PlayerState(data)
            console.log('playerState:' + JSON.stringify(playerState, null, 4))
            // renderCardsOnHand();
            renderCardsOnHandAndCheckDisabled();
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
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
            const newState = new GameState(data)
            if (newState.activePlayerUuid !== gameState.activePlayerUuid) {
                console.log("status gry sie zmienił")
                getPlayerState();
            }
            gameState = newState;
            if (!newState.equals(gameState)) {
                gameState = newState;
            }
            updateGameState();
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
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
            triggerSuccessToast('Rozpoczęto grę');
            getGameState();
            getPlayerState();
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
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
            getPlayerState();
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
        });
}

const move = () => {
    if (playerState.state !== 'ACTIVE') {
        triggerWarnToast('Nie możesz wykonać ruchu, czekaj na swoją kolej')
        putAside = [];
        updateGameState();
        getPlayerState();
        renderCardsOnHandAndCheckDisabled();
        renderPutAside();
        return;
    }
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
            // console.log('user made move')
            updateGameState();
            getPlayerState();
            // renderCardsOnHand();
            renderCardsOnHandAndCheckDisabled();
            renderPutAside();
        })
        .catch(error => {
            console.error('There was an error!', error);
            triggerErrorToast(error);
        });
}

const updateGameState = () => {
    const stateText = document.getElementById("game-state")
    stateText.innerHTML = gameState.renderGameState()
    if (gameState.gameState === "PLAYING") {
        populateLastCardOnStack(gameState.lastOnStack);
    }else {
        hideLastCardOnDeck();
    }
}

const hideLastCardOnDeck = () =>{
    const domObj = document.getElementById("last-on-stack")
    domObj.innerHTML = '';
}

const populateLastCardOnStack = (card) => {
    const domObj = document.getElementById("last-on-stack")
    const cardd = new Card(undefined, undefined, card)
    domObj.innerHTML = cardd.getCardHtml();
}

const renderCardsOnHand = () => {
    const onHand1 = document.getElementById("on-hand")
    let result = '';
    if (playerState.onHand !== undefined && playerState.onHand.length > 0) {
        playerState.onHand = playerState.onHand.map(card => new Card(undefined, undefined, card));
        playerState.onHand.forEach(card => {
            result += card.getOnHandCard();
        })
        onHand1.innerHTML = result;
    }
}

const renderCardsOnHandAndCheckDisabled = () => {
    const onHand1 = document.getElementById("on-hand")
    onHand1.innerHTML = "";
    let result = '';
    const requestedCards = playerState.requestedCardsInNextMove;
    if (requestedCards === undefined || requestedCards === null || requestedCards.length === 0) {
        console.log("requestedCards empty")
        if (playerState.onHand !== undefined && playerState.onHand.length > 0) {
            console.log("no requested cards")
            playerState.onHand = playerState.onHand.map(card => new Card(undefined, undefined, card));
            playerState.onHand.forEach(card => {
                result += card.getOnHandCard();
            })
            onHand1.innerHTML = result;
        }
    } else {
        if (playerState.onHand !== undefined && playerState.onHand.length > 0) {
            console.log("requested cards present!")
            playerState.onHand = playerState.onHand.map(card => new Card(undefined, undefined, card));
            playerState.onHand.forEach(card => {
                if (card.isRequested()) {
                    console.log("karta - aktywna")
                    result += card.getOnHandCard();
                } else {
                    console.log("karta - nieaktywna")
                    result += card.getDisabledCardOnHand();
                }
            })
            onHand1.innerHTML = result;
        }
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
    renderCardsOnHandAndCheckDisabled();
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

const setUserUuid = (uuid) => {
    localStorage.setItem("uuid", uuid);
}

const getUserUuid = () => {
    return localStorage.getItem("uuid");
}


const putCardsBackToHand = () => {
    console.log('putCardsBackToHand')
    playerState.onHand.push(...putAside);
    putAside = [];
    renderCardsOnHandAndCheckDisabled();
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

const triggerErrorToast = (message) => {
    toastr.error(message, 'Nieoczekiwany błąd')
}

const triggerSuccessToast = (message) => {
    toastr.success(message)
}

const triggerWarnToast = (message) => {
    toastr.warning(message)
}


setInterval('refreshGame()', 1000)

function refreshGame() {
    getGameState();
}

//tests

playerState.requestedCardsInNextMove = [new Card("Club", "2"), new Card("Heart", "2")]

function test() {
    const card = new Card("Club", "2");
    console.log(card.isRequested());
    const card1 = new Card("Heart", "2");
    console.log(card1.isRequested());
    const card2 = new Card("Heart", "4");
    console.log(card2.isRequested());
    const card3 = new Card("Diamond", "2");
    console.log(card3.isRequested());
}

test();