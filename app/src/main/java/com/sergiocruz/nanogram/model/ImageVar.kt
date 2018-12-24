package com.sergiocruz.nanogram.model

import com.sergiocruz.nanogram.model.endpoint.usermedia.Caption
import com.sergiocruz.nanogram.model.endpoint.usermedia.Comments
import com.sergiocruz.nanogram.model.endpoint.usermedia.Images
import com.sergiocruz.nanogram.model.endpoint.usermedia.Likes

data class ImageVar(
    var images: Images?,
    var likes: Likes? = null,
    var comments: Comments? = null,
    var caption: Caption? = null
)