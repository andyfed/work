const gulp = require('gulp');
const fs = require('fs');
const forEach = require('foreach');
const path = require('path');

const targetFolder1 = './adobe-target/';
const targetFolder2 = './js/';
const targetFolder3 = './styles/';

let files=[];

function createFolder(newFileName){
    gulp
        .src('./tasks/')
        fs.mkdirSync(newFileName)
        .pipe(gulp.dest('./tasks/'))      //create obj in this folder
}


function getFileNames(dirName) {
    fs.readdirSync(dirName).forEach(file => {
        files.push(path.basename(file, path.extname(file)));
    })
}

series(getFileNames(targetFolder1),getFileNames(targetFolder2),getFileNames(targetFolder3),createFolder(forEach(files)));

//series(createFolder(targetFolder1),createFolder(targetFolder2),createFolder(targetFolder3))