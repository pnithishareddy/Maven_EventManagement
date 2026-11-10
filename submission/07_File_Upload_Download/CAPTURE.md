# 07 File Upload / Download - screenshots
1. Postman: POST /api/events/1/brochure, Body form-data, key 'file' (type File) with a PDF chosen -> 200 with fileName, contentType, size.
2. Wrong type (e.g. .txt) -> 400 'Only PDF, PNG or JPEG files are allowed'.
3. GET /api/events/1/brochure -> 200 with the file (Send and Download), headers showing Content-Disposition: attachment.
4. Windows Explorer showing the file inside the project's uploads folder.
5. GET /api/events/1 showing hasBrochure: true.
