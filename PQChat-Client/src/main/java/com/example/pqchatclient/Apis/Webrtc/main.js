function openSteam() {
    const config = {audio : true, video : true};
    return navigator.mediaDevices.getUserMedia(config);
}
function playSteam(idVideoTag, steam){
    const video = document.getElementById(idVideoTag);
    video.srcObject = steam;
    video.play();
}



openSteam()
.then(stream => playSteam("localStream", stream))