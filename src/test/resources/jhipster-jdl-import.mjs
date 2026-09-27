// Imports JDL files with the JHipster JDL parser, to check that the
// files written by xmi-to-jdl are accepted.
//
// Usage: node jhipster-jdl-import.mjs <node_modules folder> <jdl file>...
// Exits 1 when any file is refused, so that a test can assert on it.
import { readFileSync } from 'node:fs';

const parserDir = process.argv[2];
const files = process.argv.slice(3);

const { createImporterFromContent } = await import(
    parserDir + '/generator-jhipster/dist/lib/jdl/jdl-importer.js');

let refused = false;
for (const file of files) {
    try {
        const state = createImporterFromContent(readFileSync(file, 'utf8'),
            { applicationName: 'test' }).import();
        console.log('OK ' + file + ' entities=' + (state.exportedEntities || []).length);
    } catch (error) {
        refused = true;
        console.log('REFUSED ' + file + ': ' + String(error.message).split('\n')[0]);
    }
}
process.exit(refused ? 1 : 0);
