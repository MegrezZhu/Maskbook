# Maskbook Server

## Development Guide

* In VSCode, use `Tasks: Run Task` and:
  * select `tsc: watch - tsconfig.json` to start TypeScript live-compiling.
  * select `tsc: build - tsconfig.json` for one-time compiling.
* Compiled `.js` files are in `./dist`.
* If some modules lack definition, simply add the following stub code in `./src/@types` with file name `xxx.d.ts`, in which `xxx` is arbitary:
```typescript
declare module 'MODULE-NAME';
```
* Recommended VSCode Extensions:
  * TSLint
    * enable autofix: `"tslint.autoFixOnSave": true`
    * to avoid conflicts with editor's autofix `"editor.formatOnSave": true`, add both `"typescript.format.insertSpaceBeforeFunctionParenthesis": true` & `"typescript.format.insertSpaceAfterConstructor": true`
